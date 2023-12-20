package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

class SaveFileEvent(val virtualDescriptor: VirtualDescriptor): FilesystemChangeEvent(FileChangeType.SAVE)