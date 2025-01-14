package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

data class RenameEvent(val item: VirtualDescriptor, val newName: String): InternalChangeEvent(FileChangeType.RENAME)