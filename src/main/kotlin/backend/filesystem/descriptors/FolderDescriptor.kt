package backend.filesystem.descriptors

import backend.filesystem.VirtualFile

class FolderDescriptor(
    override var name: String,
    override val type: VirtualDescriptorFileType
) : VirtualDescriptor {

    override fun getFile(): VirtualFile {
        TODO("Not yet implemented")
    }

}