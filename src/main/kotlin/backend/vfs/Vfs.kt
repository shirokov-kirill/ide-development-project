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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.io.path.Path
import kotlin.io.path.name

class Vfs(private val filesystemMonitor: FilesystemMonitor,
          private val cacheManager: CacheManager?,
          private val filesystemEvents: ConcurrentLinkedQueue<FilesystemChangeEvent>,
          private val innerEventsQueue: ConcurrentLinkedQueue<FilesystemChangeEvent>,
): Cacheable {
    // other data
    private var projectPath: String = ""
    private var folderStructureTree = UpdatableFolderStructureTree()
    // --API-related
    private val _folderTree = MutableStateFlow<FolderStructureNode>(UpdatableFolderStructureTreeNode.Empty)
    // private val _virtualFolderTree = MutableStateFlow<FolderStructureNode>(TODO()

    // event handlers
    private val innerChangesHandlingThread = Thread { innerChangesHandler() }
    private val outerChangesHandlingThread = Thread { outerChangesHandler() }

    override var cacheableData: Any = ""
    override var cacheFile: File = File(projConfigFolderName + vfsConfigFileName)

    // API elements
    val folderTree: StateFlow<FolderStructureNode> = _folderTree.asStateFlow()
    val virtualFolderTree: StateFlow<FolderStructureNode> = _folderTree.asStateFlow() //TODO()

    init {
        innerChangesHandlingThread.start()
        outerChangesHandlingThread.start()

        // register to cache the data
        CacheWriter.SimpleCacheWriter.register(this)
    }

    private fun loadHandler(filePath: String) {
        filesystemMonitor.reset()
        if(load(filePath)) {
            filesystemMonitor.register(Path(filePath))
        }
    }

    private fun createFileHandler(event: CreateFileEvent) {
        folderStructureTree.add(event.parent, FileDescriptor(event.fileName, FileLike(event.parent.getFile().path.resolve(event.fileName).toFile())))
        _folderTree.update { folderStructureTree.root }
        cacheableData = folderStructureTree.root
    }

    private fun createFolderHandler(event: CreateFolderEvent) {
        folderStructureTree.add(event.parent, FolderDescriptor(event.name, FolderLike(event.parent.getFile().path.resolve(event.name).toFile()), VirtualDescriptorFileType.Folder))
        _folderTree.update { folderStructureTree.root }
        cacheableData = folderStructureTree.root
    }

    private fun removeFileHandler(event: RemoveEvent) {
        TODO()
    }

    private fun renameFileHandler(event: RenameEvent) {
        TODO()
    }

    private fun editFileHandler(event: EditEvent) {
        TODO()
    }

    private fun innerChangesHandler() {
        val writeLock = readWriteLock.writeLock()
        while (true) {
            writeLock.lock()
            while (innerEventsQueue.peek() != null) {
                val element = innerEventsQueue.poll()
                when(element.eventType) {
                    FileChangeType.OPEN_PROJECT -> loadHandler((element as OpenProjectEvent).absoluteProjectPath)
                    FileChangeType.CREATE_FILE -> createFileHandler(element as CreateFileEvent)
                    FileChangeType.CREATE_FOLDER -> createFolderHandler(element as CreateFolderEvent)
                    FileChangeType.REMOVE -> removeFileHandler(element as RemoveEvent)
                    FileChangeType.RENAME -> renameFileHandler(element as RenameEvent)
                    FileChangeType.EDIT -> editFileHandler(element as EditEvent)
                    else -> continue // ignore all unrelated messages
                }
            }
            writeLock.unlock()
            Thread.sleep(500)
        }
    }

    private fun outerChangesHandler() {
        val writeLock = readWriteLock.writeLock()
        while (true) {
            writeLock.lock()
            while (filesystemEvents.peek() != null) {
                val element = filesystemEvents.poll()
                when(element.eventType) {
                    FileChangeType.CREATE -> loadHandler((element as OpenProjectEvent).absoluteProjectPath)
                    FileChangeType.EDIT -> continue //TODO()
                    FileChangeType.REMOVE -> continue //TODO()
                    else -> continue // ignore all unrelated messages
                }
            }
            writeLock.unlock()
            Thread.sleep(500)
        }
    }

    private fun load(filePath: String): Boolean {
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
        private val readWriteLock = ReentrantReadWriteLock()
    }
}