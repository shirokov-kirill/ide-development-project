package backend.filesystem.descriptors

import backend.filesystem.VirtualDescriptorFileType
import backend.filesystem.VirtualFile

class FileDescriptor(
    override val name: String,
    override val relativePath: String,
) : VirtualDescriptor {
    override val type: VirtualDescriptorFileType = VirtualDescriptorFileType.File

    override fun getFile(): VirtualFile {
        TODO("Not yet implemented")
    }
}