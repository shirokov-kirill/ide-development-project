package backend.filesystem.descriptors

import backend.filesystem.VirtualFile

interface VirtualDescriptor {
    var name: String
    val type: VirtualDescriptorFileType

    fun getFile(): VirtualFile?

    companion object {
        val Empty = object : VirtualDescriptor {
            override var name: String = ""
            override val type: VirtualDescriptorFileType
                get() = VirtualDescriptorFileType.Empty

            override fun getFile(): VirtualFile? {
                return null
            }

        }
    }
}