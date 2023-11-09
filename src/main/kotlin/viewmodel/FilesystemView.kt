package viewmodel

import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow

interface FilesystemView {
    val folderStructure: StateFlow<FolderStructureNode>
    val virtualFolderStructure: StateFlow<FolderStructureNode>

    fun openFolder(absoluteFolderPath: String)
    fun closeProject()
    fun createNewFile(relativePath: String)
    fun createNewFolder(relativePath: String)
    fun renameFile(relativePath: String, newName: String)
    fun deleteFile(relativePath: String)
}