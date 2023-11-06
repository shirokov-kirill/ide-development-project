package backend.vfs

import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow
import java.lang.reflect.Modifier

interface FileManager {
    val folderTree: StateFlow<FolderStructureNode>
    val virtualFolderTree: StateFlow<FolderStructureNode>

    fun save(descriptor: VirtualDescriptor): Boolean

    fun modify(descriptor: VirtualDescriptor, data: String): Boolean

    fun delete(descriptor: VirtualDescriptor): Boolean

    fun load(filePath: String): Boolean

    fun create(filePath: String, modifier: Int): Boolean
}