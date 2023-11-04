package backend.filesystem.descriptors

import backend.filesystem.VirtualDescriptorFileType
import backend.filesystem.VirtualFile

class FolderDescriptor(
    override val name: String,
    override val relativePath: String,
    override val type: VirtualDescriptorFileType
) : VirtualDescriptor {

    override fun getFile(): VirtualFile {
        TODO("Not yet implemented")
    }

}