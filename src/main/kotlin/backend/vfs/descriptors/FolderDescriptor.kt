package backend.vfs.descriptors

import backend.vfs.files.FolderLike
import backend.vfs.files.VirtualFile
import java.io.File

class FolderDescriptor(
    override var name: String,
    private val file: FolderLike,
    override val type: VirtualDescriptorFileType
) : VirtualDescriptor {

    override fun getFile(): VirtualFile {
        return file
    }

}