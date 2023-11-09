package backend.vfs

import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.structure.FolderStructureNode
import kotlinx.coroutines.flow.StateFlow
import java.lang.reflect.Modifier

interface FileManager {
    val folderTree: StateFlow<FolderStructureNode>
    val virtualFolderTree: StateFlow<FolderStructureNode>

    fun save(filePath: String): Boolean

    fun delete(filePath: String): Boolean

    fun load(absoluteFolderPath: String): Boolean

    fun create(filePath: String, modifier: Int): Boolean

    fun rename(filePath: String, newName: String)

    fun close()

    companion object {
        public const val CREATE_FILE: Int = 0
        public const val CREATE_DIR: Int = 1
    }
}