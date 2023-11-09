package backend.vfs

import backend.filesystem.FilesystemMonitor
import backend.filesystem.events.*
import backend.vfs.FileManager.Companion.CREATE_DIR
import backend.vfs.FileManager.Companion.CREATE_FILE
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.io.path.Path

class IDELangFileManager: FileManager {
    private val fileSystemEventChannel = ConcurrentLinkedQueue<FilesystemChangeEvent>()
    private val innerEventsQueue = ConcurrentLinkedQueue<FilesystemChangeEvent>()

    private val vfs = Vfs(FilesystemMonitor(fileSystemEventChannel), fileSystemEventChannel, innerEventsQueue)

    override val folderTree: StateFlow<FolderStructureNode> = vfs.folderTree
    override val virtualFolderTree: StateFlow<FolderStructureNode> = vfs.virtualFolderTree

    /*
     * Call on explicit Save action from user
     */
    override fun save(filePath: String) {
        TODO("Not yet implemented")
    }

    override fun delete(filePath: String) {
        innerEventsQueue.add(RemoveEvent(filePath))
    }

    /*
     * Call on explicit Open Folder action from user
     */
    override fun load(absoluteFolderPath: String) {
        innerEventsQueue.add(OpenProjectEvent(absoluteFolderPath))
    }

    /*
     * Call on explicit Create File/Folder action from user
     */
    override fun create(filePath: String, name: String, modifier: Int) {
        when(modifier) {
            CREATE_FILE -> {
                innerEventsQueue.add(CreateFileEvent(filePath, name))
            }
            CREATE_DIR -> {
                innerEventsQueue.add(CreateFolderEvent(filePath, name))
            }
            else -> {
                System.err.println("Non-supported modifier $modifier in IDELangFileManager.create method.")
            }
        }
    }

    override fun rename(filePath: String, newName: String) {
        innerEventsQueue.add(RenameEvent(Path(filePath), newName))
    }

    override fun close() {
        innerEventsQueue.add(CloseProjectEvent())
    }
}