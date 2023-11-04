package backend.filesystem.descriptors

import backend.filesystem.VirtualDescriptorFileType
import backend.filesystem.VirtualFile

interface VirtualDescriptor {
    val name: String
    val relativePath: String
    val type: VirtualDescriptorFileType

    fun getFile(): VirtualFile?

    companion object {
        val Empty = object : VirtualDescriptor {
            override val name: String
                get() = ""
            override val relativePath: String
                get() = ""
            override val type: VirtualDescriptorFileType
                get() = VirtualDescriptorFileType.Empty

            override fun getFile(): VirtualFile? {
                return null
            }

        }
    }
}