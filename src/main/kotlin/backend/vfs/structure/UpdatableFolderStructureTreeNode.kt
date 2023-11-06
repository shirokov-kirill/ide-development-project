package backend.vfs.structure

import backend.vfs.descriptors.VirtualDescriptor

data class UpdatableFolderStructureTreeNode(
    var hash: Int,
    override val virtualDescriptor: VirtualDescriptor,
    override val children: List<FolderStructureNode>): FolderStructureNode {
        companion object {
            val Empty = UpdatableFolderStructureTreeNode(0, VirtualDescriptor.Empty, listOf())
        }
    }
