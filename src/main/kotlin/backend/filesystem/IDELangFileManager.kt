package backend.filesystem

import backend.filesystem.descriptors.VirtualDescriptor
import backend.filesystem.structure.FolderStructureNode
import backend.filesystem.structure.UpdatableFolderStructureTree
import backend.filesystem.structure.UpdatableFolderStructureTreeNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IDELangFileManager: FileManager {

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
        return true
    }

}