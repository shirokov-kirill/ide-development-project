package backend.filesystem

import backend.filesystem.descriptors.VirtualDescriptor
import backend.filesystem.descriptors.VirtualDescriptorFileType
import backend.filesystem.structure.FolderStructureNode
import backend.filesystem.structure.UpdatableFolderStructureTree
import backend.filesystem.structure.UpdatableFolderStructureTreeNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class IDELangFileManager: FileManager {
    private var projectPath = ""
    private var folderStructureTree = UpdatableFolderStructureTree()
    private val _folderTree = MutableStateFlow<FolderStructureNode>(UpdatableFolderStructureTreeNode.Empty)
    override val folderTree: StateFlow<FolderStructureNode> = _folderTree.asStateFlow()
    // private val _virtualFolderTree
    // val virtualFolderTree

    override fun save(descriptor: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    override fun modify(descriptor: VirtualDescriptor, data: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(descriptor: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    override fun load(filePath: String): Boolean {
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
        val configFolderPath = listOf(projectPath, projConfigFolderName).joinToString("/")
        val configFolder = File(configFolderPath)
        try {
            configFolder.mkdir()
            initializeConfigFile(configFolderPath)
        } catch (e: SecurityException) {
            println("Can't initialize project due to security reasons.")
        }
    }

    private fun initializeConfigFile(path: String) {
        val configFile = File(listOf(path, projConfigFileName).joinToString("/"))
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