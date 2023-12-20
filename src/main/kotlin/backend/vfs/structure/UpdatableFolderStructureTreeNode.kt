package backend.vfs.structure

import backend.vfs.descriptors.VirtualDescriptor

data class UpdatableFolderStructureTreeNode(
    var hash: Int,
    override val virtualDescriptor: VirtualDescriptor,
    override val children: MutableList<FolderStructureNode>): FolderStructureNode {

    val fileName: String
        get() = virtualDescriptor.name
    companion object {
        val Empty = UpdatableFolderStructureTreeNode(0, VirtualDescriptor.Empty, mutableListOf())
    }
}
