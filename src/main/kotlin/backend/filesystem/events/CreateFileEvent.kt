package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

data class CreateFileEvent(val parent: VirtualDescriptor, val fileName: String): FilesystemChangeEvent(FileChangeType.CREATE_FILE)