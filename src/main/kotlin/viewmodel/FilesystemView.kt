package viewmodel

import backend.vfs.descriptors.FolderDescriptor
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow

interface FilesystemView {
    val folderStructure: StateFlow<FolderStructureNode>
    val virtualFolderStructure: StateFlow<FolderStructureNode>

    fun openFolder(absoluteFolderPath: String)
    fun closeProject()
    fun createNewFile(parent: FolderDescriptor, name: String)
    fun createNewFolder(parent: FolderDescriptor, name: String)
    fun renameFile(item: VirtualDescriptor, newName: String)
    fun deleteFile(item: VirtualDescriptor)
    fun saveFile(item: VirtualDescriptor)
}