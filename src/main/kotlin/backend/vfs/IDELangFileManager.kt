package backend.vfs

import backend.filesystem.FileChangeType
import backend.filesystem.FilesystemChangeEvent
import backend.filesystem.FilesystemMonitor
import backend.vfs.FileManager.Companion.CREATE_DIR
import backend.vfs.FileManager.Companion.CREATE_FILE
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.descriptors.VirtualDescriptorFileType
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.Path

class IDELangFileManager: FileManager {
    private val fileSystemEventChannel = Channel<FilesystemChangeEvent>(Channel.UNLIMITED)
    private val innerEventsChannel = Channel<FilesystemChangeEvent>(1000)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val eventSenderScope = Dispatchers.IO.limitedParallelism(1)

    private val filesystemMonitor = FilesystemMonitor(fileSystemEventChannel)
    private val vfs = Vfs(fileSystemEventChannel, innerEventsChannel)

    override val folderTree: StateFlow<FolderStructureNode> = vfs.folderTree
    override val virtualFolderTree: StateFlow<FolderStructureNode> = vfs.virtualFolderTree

    /*
     * Call on explicit Save action from user
     */
    override fun save(filePath: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(filePath: String): Boolean {
        TODO("Not yet implemented")
    }

    /*
     * Call on explicit Open Folder action from user
     */
    override fun load(absoluteFolderPath: String): Boolean {
        if(vfs.load(absoluteFolderPath)) {
            filesystemMonitor.register(Path(absoluteFolderPath))
            return true
        }
        return false
    }

    /*
     * Call on explicit Create File/Folder action from user
     */
    override fun create(filePath: String, modifier: Int): Boolean {
        when(modifier) {
            CREATE_FILE -> {
                CoroutineScope(eventSenderScope). launch {
                    innerEventsChannel.send(FilesystemChangeEvent(FileChangeType.LOCAL_CREATE_FILE, File(filePath).toPath()))
                }
            }
            CREATE_DIR -> {
                CoroutineScope(eventSenderScope). launch {
                    innerEventsChannel.send(FilesystemChangeEvent(FileChangeType.LOCAL_CREATE_FOLDER, File(filePath).toPath()))
                }
            }
            else -> {
                System.err.println("Non-supported modifier $modifier in IDELangFileManager.create method.")
            }
        }
        return true
    }

    override fun rename(filePath: String, newName: String) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}