package backend.vfs

import backend.filesystem.FilesystemChangeEvent
import backend.vfs.descriptors.VirtualDescriptorFileType
import backend.vfs.structure.FolderStructureNode
import backend.vfs.structure.UpdatableFolderStructureTree
import backend.vfs.structure.UpdatableFolderStructureTreeNode
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class VfsManager(private val filesystemEvents: ReceiveChannel<FilesystemChangeEvent>) {
    private var projectPath: String = ""
    private var folderStructureTree = UpdatableFolderStructureTree()
    private val _folderTree = MutableStateFlow<FolderStructureNode>(UpdatableFolderStructureTreeNode.Empty)
    // private val _virtualFolderTree = MutableStateFlow<FolderStructureNode>(TODO()

    val folderTree: StateFlow<FolderStructureNode> = _folderTree.asStateFlow()
    val virtualFolderTree: StateFlow<FolderStructureNode> = _folderTree.asStateFlow() //TODO()

    fun load(filePath: String): Boolean {
        _folderTree.update { folderStructureTree.load(filePath) }
        if(folderStructureTree.root.virtualDescriptor.type != VirtualDescriptorFileType.Empty) {
            // this is a valid path
            projectPath = filePath
        }
        if(folderStructureTree.root.virtualDescriptor.type == VirtualDescriptorFileType.RootFolder) {
            initializeProjectIfNotYet()
        }

        return true
    }

    private fun initializeProjectIfNotYet() {
        // create config folder
        val configFolderPath = listOf(projectPath, VfsManager.projConfigFolderName).joinToString("/")
        val configFolder = File(configFolderPath)
        try {
            configFolder.mkdir()
            initializeConfigFile(configFolderPath)
        } catch (e: SecurityException) {
            println("Can't initialize project due to security reasons.")
        }
    }

    private fun initializeConfigFile(path: String) {
        val configFile = File(listOf(path, VfsManager.projConfigFileName).joinToString("/"))
        configFile.createNewFile()
        updateFileAsConfig(configFile)
    }

    private fun updateFileAsConfig(file: File) {
        file.writeText("proj_dir: $projectPath")
    }

    companion object {
        const val projConfigFolderName = ".idl"
        const val projConfigFileName = "config.txt"
    }
}