package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

data class CreateFileEvent(val parent: VirtualDescriptor, val fileName: String): InternalChangeEvent(FileChangeType.CREATE_FILE)