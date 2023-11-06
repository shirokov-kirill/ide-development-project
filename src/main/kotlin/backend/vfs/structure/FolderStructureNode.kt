package backend.vfs.structure

import backend.vfs.descriptors.VirtualDescriptor

interface FolderStructureNode {
    val virtualDescriptor: VirtualDescriptor
    val children: List<FolderStructureNode>
}