package backend.vfs

import backend.vfs.descriptors.FileDescriptor
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow
import java.lang.reflect.Modifier

interface FileManager {
    val folderTree: StateFlow<FolderStructureNode>
    val virtualFolderTree: StateFlow<FolderStructureNode>

    /*
     * Call on explicit Save action from user
     */
    fun save(filePath: String)

    fun delete(item: VirtualDescriptor)

    /*
     * Call on explicit Open Folder action from user
     */
    fun load(absoluteFolderPath: String)

    /*
     * Call on explicit Create File/Folder action from user
     */
    fun create(parent: VirtualDescriptor, name: String, modifier: Int)

    fun rename(item: VirtualDescriptor, newName: String)

    fun close()

    companion object {
        public const val CREATE_FILE: Int = 0
        public const val CREATE_DIR: Int = 1
    }
}