package backend.vfs.structure

import backend.vfs.descriptors.VirtualDescriptor

interface FolderStructure {
    fun add(item: VirtualDescriptor): Boolean
    fun remove(item: VirtualDescriptor): Boolean
    fun load(filePath: String): FolderStructureNode
    fun isEmpty(): Boolean
}