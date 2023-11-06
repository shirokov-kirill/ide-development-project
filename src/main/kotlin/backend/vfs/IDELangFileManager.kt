package backend.vfs

import backend.filesystem.FilesystemChangeEvent
import backend.filesystem.FilesystemMonitor
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.descriptors.VirtualDescriptorFileType
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.io.path.Path

class IDELangFileManager: FileManager {
    private val fileSystemEventChannel = Channel<FilesystemChangeEvent>(Channel.UNLIMITED)
    private val filesystemMonitor = FilesystemMonitor(fileSystemEventChannel)
    private val vfsManager = VfsManager(fileSystemEventChannel)

    override val folderTree: StateFlow<FolderStructureNode> = vfsManager.folderTree
    override val virtualFolderTree: StateFlow<FolderStructureNode> = vfsManager.virtualFolderTree

    /*
     * Call on explicit Save action from user
     */
    override fun save(descriptor: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    override fun modify(descriptor: VirtualDescriptor, data: String): Boolean {
        TODO()
    }

    override fun delete(descriptor: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    /*
     * Call on explicit Open Folder action from user
     */
    override fun load(filePath: String): Boolean {
        if(vfsManager.load(filePath)) {
            filesystemMonitor.register(Path(filePath))
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

            }
            CREATE_DIR -> {
                return load(filePath)
            }
            else -> {
                System.err.println("Non-supported modifier $modifier in IDELangFileManager.create method.")
            }
        }
        return true
    }

    companion object {
        public const val CREATE_FILE: Int = 0
        public const val CREATE_DIR: Int = 1
    }
}