package backend.vfs.descriptors

import backend.vfs.files.VirtualFile

interface VirtualDescriptor {
    var name: String
    val type: VirtualDescriptorFileType

    fun getFile(): VirtualFile

    companion object {
        val Empty = object : VirtualDescriptor {
            override var name: String = ""
            override val type: VirtualDescriptorFileType
                get() = VirtualDescriptorFileType.Empty

            override fun getFile(): VirtualFile {
                return VirtualFile.Empty
            }

        }
    }
}