package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

class DeleteEvent(val descriptor: VirtualDescriptor): FilesystemChangeEvent(FileChangeType.REMOVE)