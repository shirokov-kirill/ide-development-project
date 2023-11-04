package backend.filesystem.structure

import backend.filesystem.descriptors.VirtualDescriptor

interface FolderStructureNode {
    val virtualDescriptor: VirtualDescriptor
    val children: List<FolderStructureNode>
}