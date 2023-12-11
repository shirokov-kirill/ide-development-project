package backend.vfs.descriptors

import backend.vfs.files.FileLike
import backend.vfs.files.VirtualFile
import java.io.File

class FileDescriptor(
    override var name: String,
    private val file: FileLike
) : VirtualDescriptor {
    override val type: VirtualDescriptorFileType = VirtualDescriptorFileType.File

    override fun getFile(): VirtualFile {
        return file
    }
}