package backend.filesystem

import backend.filesystem.descriptors.VirtualDescriptor
import backend.filesystem.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow

interface FileManager {
    val folderTree: StateFlow<FolderStructureNode>
    val virtualFolderTree: StateFlow<FolderStructureNode>

    fun save(descriptor: VirtualDescriptor): Boolean

    fun modify(descriptor: VirtualDescriptor, data: String): Boolean

    fun delete(descriptor: VirtualDescriptor): Boolean

    fun load(filePath: String): Boolean
}