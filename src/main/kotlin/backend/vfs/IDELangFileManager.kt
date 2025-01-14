package backend.vfs

import backend.filesystem.CacheManager
import backend.filesystem.FilesystemMonitor
import backend.filesystem.events.*
import backend.psi.PsiManager
import backend.psi.lexer.idelang.IDELangLexer
import backend.vfs.FileManager.Companion.CREATE_DIR
import backend.vfs.FileManager.Companion.CREATE_FILE
import backend.vfs.descriptors.FileDescriptor
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.structure.FolderStructureNode
import backend.vfs.structure.UpdatableFolderStructureTreeNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.io.path.Path

class IDELangFileManager(private val cacheManager: CacheManager?): FileManager {
    private val fileSystemEventChannel = ConcurrentLinkedQueue<ExternalChangeEvent>()
    private val innerEventsQueue = ConcurrentLinkedQueue<InternalChangeEvent>()

    private val vfs = Vfs(FilesystemMonitor(fileSystemEventChannel), cacheManager, fileSystemEventChannel, innerEventsQueue)
    private val psiComponent = PsiManager(vfs, cacheManager)

    override val folderTree: StateFlow<FolderStructureNode> = vfs.folderTree
    override val virtualFolderTree: StateFlow<FolderStructureNode> = vfs.virtualFolderTree

    override fun save(item: VirtualDescriptor) {
        innerEventsQueue.add(SaveFileEvent(item))
    }

    override fun delete(item: VirtualDescriptor) {
        innerEventsQueue.add(DeleteEvent(item))
    }

    override fun load(absoluteFolderPath: String) {
        innerEventsQueue.add(OpenProjectEvent(absoluteFolderPath))
    }

    override fun create(parent: VirtualDescriptor, name: String, modifier: Int) {
        when(modifier) {
            CREATE_FILE -> {
                innerEventsQueue.add(CreateFileEvent(parent, name))
            }
            CREATE_DIR -> {
                innerEventsQueue.add(CreateFolderEvent(parent, name))
            }
            else -> {
                System.err.println("Non-supported modifier $modifier in IDELangFileManager.create method.")
            }
        }
    }

    override fun rename(item: VirtualDescriptor, newName: String) {
        innerEventsQueue.add(RenameEvent(item, newName))
    }

    override fun close() {
        innerEventsQueue.add(CloseProjectEvent())
    }
}