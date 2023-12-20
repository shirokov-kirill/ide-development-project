package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor
data class DeleteEvent(val virtualDescriptor: VirtualDescriptor): InternalChangeEvent(FileChangeType.REMOVE)