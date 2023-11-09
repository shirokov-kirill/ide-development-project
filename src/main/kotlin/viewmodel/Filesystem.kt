package viewmodel

import backend.vfs.FileManager
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow

class Filesystem(private val fileManager: FileManager): FilesystemView {
    override val folderStructure: StateFlow<FolderStructureNode> = fileManager.folderTree
    override val virtualFolderStructure: StateFlow<FolderStructureNode> = fileManager.virtualFolderTree

    override fun openFolder(absoluteFolderPath: String) {
        fileManager.load(absoluteFolderPath)
    }

    override fun closeProject() {
        fileManager.close()
    }

    override fun createNewFile(relativePath: String, name: String) {
        fileManager.create(relativePath, name, FileManager.CREATE_FILE)
    }

    override fun createNewFolder(relativePath: String, name: String) {
        fileManager.create(relativePath, name, FileManager.CREATE_DIR)
    }

    override fun renameFile(relativePath: String, newName: String) {
        fileManager.rename(relativePath, newName)
    }

    override fun deleteFile(relativePath: String) {
        fileManager.delete(relativePath)
    }
}