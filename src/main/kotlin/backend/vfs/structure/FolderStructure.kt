package backend.vfs.structure

import backend.vfs.descriptors.VirtualDescriptor

interface FolderStructure {
    fun add(parent: VirtualDescriptor, item: VirtualDescriptor): Boolean
    fun remove(item: VirtualDescriptor): Boolean
    fun load(filePath: String): FolderStructureNode
    fun isEmpty(): Boolean
    fun find(filePath: String): FolderStructureNode
    fun reloadSubtree(projectPath: String, path: String): Boolean
}