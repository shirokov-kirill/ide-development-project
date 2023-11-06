package backend.vfs.descriptors

import backend.vfs.VirtualFile

class FileDescriptor(
    override var name: String,
) : VirtualDescriptor {
    override val type: VirtualDescriptorFileType = VirtualDescriptorFileType.File

    override fun getFile(): VirtualFile {
        TODO("Not yet implemented")
    }
}