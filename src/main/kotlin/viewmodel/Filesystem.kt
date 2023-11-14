package viewmodel

import backend.vfs.FileManager
import backend.vfs.descriptors.FolderDescriptor
import backend.vfs.descriptors.VirtualDescriptor
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

    override fun createNewFile(parent: FolderDescriptor, name: String) {
        fileManager.create(parent, name, FileManager.CREATE_FILE)
    }

    override fun createNewFolder(parent: FolderDescriptor, name: String) {
        fileManager.create(parent, name, FileManager.CREATE_DIR)
    }

    override fun renameFile(item: VirtualDescriptor, newName: String) {
        fileManager.rename(item, newName)
    }

    override fun deleteFile(item: VirtualDescriptor) {
        fileManager.delete(item)
    }
}