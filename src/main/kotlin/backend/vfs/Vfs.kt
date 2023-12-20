package backend.vfs

import backend.cache.CacheWriter
import backend.cache.Cacheable
import backend.filesystem.CacheManager
import backend.filesystem.FilesystemMonitor
import backend.filesystem.events.*
import backend.vfs.descriptors.FileDescriptor
import backend.vfs.descriptors.FolderDescriptor
import backend.vfs.descriptors.VirtualDescriptorFileType
import backend.vfs.files.FileLike
import backend.vfs.files.FolderLike
import backend.vfs.structure.FolderStructureNode
import backend.vfs.structure.UpdatableFolderStructureTree
import backend.vfs.structure.UpdatableFolderStructureTreeNode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.io.path.Path
import kotlin.io.path.pathString

class Vfs(private val filesystemMonitor: FilesystemMonitor,
          private val cacheManager: CacheManager?,
          private val filesystemEvents: ConcurrentLinkedQueue<ExternalChangeEvent>,
          private val innerEventsQueue: ConcurrentLinkedQueue<InternalChangeEvent>,
): Cacheable {
    // lock
    public val readWriteLock = ReentrantReadWriteLock()
    // other data
    private var projectPath: String = ""
    private var folderStructureTree = UpdatableFolderStructureTree()
    // --API-related
    private val _folderTree = MutableStateFlow(UpdatableFolderStructureTreeNode.Empty)

    public val watches: MutableList<Pair<FileDescriptor, Boolean>> = mutableListOf()

    // event handlers
    private val vfsScope = CoroutineScope(Dispatchers.IO + CoroutineName("Vfs"))

    override var cacheableData: Any = ""
    override var cacheFile: File = File(projConfigFolderName + vfsConfigFileName)

    // API elements
    val folderTree: StateFlow<FolderStructureNode> = _folderTree.asStateFlow()
    val virtualFolderTree: StateFlow<FolderStructureNode> = _folderTree.asStateFlow() //TODO()

    init {
        vfsScope.launch {
            innerChangesHandler()
        }

        vfsScope.launch {
            outerChangesHandler()
        }

        // register to cache the data
        CacheWriter.SimpleCacheWriter.register(this)
    }

    private suspend fun loadHandler(event: OpenProjectEvent) {
        filesystemMonitor.reset()
        if(load(event.absoluteProjectPath)) {
            filesystemMonitor.register(Path(event.absoluteProjectPath))
        }
    }

    private fun createFileHandler(event: CreateFileEvent) {
        event.parent.getFile().path.resolve(event.fileName).toFile().createNewFile()
    }

    private fun createFolderHandler(event: CreateFolderEvent) {
        event.parent.getFile().path.resolve(event.name).toFile().mkdir()
    }

    private fun createExtHandler(event: CreateExtEvent) {
        val path = event.realPath.removePrefix(projectPath)
        folderStructureTree.reloadSubtree(projectPath, path)
    }

    private suspend fun removeFileHandler(event: DeleteEvent) {
        val file = event.virtualDescriptor
            .getFile()
            .path
            .toFile()
        file.deleteRecursively()
    }

    private suspend fun removeExtEvent(event: DeleteExtEvent) {
        try {
            val virtualDescriptor = folderStructureTree.find(event.realPath.removePrefix(projectPath)).virtualDescriptor
            folderStructureTree.remove(virtualDescriptor)
        } catch (e: IllegalArgumentException) {
            // we didn't have it already
            return
        }
    }

    private suspend fun renameFileHandler(event: RenameEvent) {
        val oldPath = event.item.getFile().path
        val parentPath = event.item
            .getFile().path
            .pathString
            .split("/")
            .dropLast(1)
            .joinToString("/")
        println(oldPath)
        val result = oldPath.toFile().renameTo(File(parentPath + "/${event.newName}"))
        if(!result) {
            println("Couldn't change the file name")
        }
    }

    private suspend fun editFileHandler(event: EditEvent) {
        TODO()
    }

    private suspend fun editFileExtHandler(event: ModifyExtEvent) {
        TODO()
    }

    private suspend fun saveEventHandler(event: SaveFileEvent) {
        val descriptor = event.virtualDescriptor
        val filePath = descriptor.getFile().path
        val fileContent = descriptor.getFile().getFileContent() ?: return
        filePath.toFile().writeText(fileContent)
    }

    private suspend fun innerChangesHandler() {
        val writeLock = readWriteLock.writeLock()
        while (true) {
            writeLock.lock()
            while (innerEventsQueue.peek() != null) {
                val element = innerEventsQueue.poll()
                when(element.eventType) {
                    FileChangeType.OPEN_PROJECT -> loadHandler(element as OpenProjectEvent)
                    FileChangeType.CREATE_FILE -> createFileHandler(element as CreateFileEvent)
                    FileChangeType.CREATE_FOLDER -> createFolderHandler(element as CreateFolderEvent)
                    FileChangeType.REMOVE -> removeFileHandler(element as DeleteEvent)
                    FileChangeType.RENAME -> renameFileHandler(element as RenameEvent)
                    FileChangeType.EDIT -> editFileHandler(element as EditEvent)
                    FileChangeType.SAVE -> saveEventHandler(element as SaveFileEvent)
                    else -> continue // ignore all unrelated messages
                }
            }
            writeLock.unlock()
            delay(500)
        }
    }

    private suspend fun outerChangesHandler() {
        val writeLock = readWriteLock.writeLock()
        while (true) {
            writeLock.lock()
            while (filesystemEvents.peek() != null) {
                val element = filesystemEvents.poll()
                when(element.eventType) {
                    FileChangeType.CREATE_EXT -> createExtHandler(element as CreateExtEvent)
                    FileChangeType.EDIT_EXT -> {
                        println(element.eventType)
                        println((element as ModifyExtEvent).realPath)
                    }//editFileExtHandler(element as ModifyExtEvent)
                    FileChangeType.REMOVE_EXT -> removeExtEvent(element as DeleteExtEvent)
                    else -> continue // ignore all unrelated messages
                }
            }
            writeLock.unlock()
            delay(500)
        }
    }

    private suspend fun load(filePath: String): Boolean {
        return if (File(filePath).exists() && File(filePath).isDirectory) {
            // path of existing folder
            _folderTree.update { folderStructureTree.load(filePath) }
            cacheableData = folderStructureTree.root
            if(folderStructureTree.root.virtualDescriptor.type != VirtualDescriptorFileType.Empty) {
                // this is a valid path
                projectPath = filePath
            }
            initializeProjectIfNotYet()

            true
        } else {
            false
        }
    }

    private fun initializeProjectIfNotYet() {
        // create config folder
        val configFolderPath = listOf(projectPath, projConfigFolderName).joinToString("/")
        val configFolder = File(configFolderPath)
        try {
            configFolder.mkdir()
            initializeConfigFile(configFolderPath)
        } catch (e: SecurityException) {
            println("Can't initialize project due to security reasons.")
        }
    }

    private fun initializeConfigFile(configFolderPath: String) {
        val configFile = File(listOf(configFolderPath, projConfigFileName).joinToString("/"))
        configFile.createNewFile()
        updateFileAsConfig(configFile)
    }

    private fun updateFileAsConfig(file: File) {
        file.writeText("proj_dir: $projectPath")
    }

    companion object {
        const val projConfigFolderName = ".idl"
        const val projConfigFileName = "config.txt"
        private const val vfsConfigFileName = "vfs.txt"
    }
}