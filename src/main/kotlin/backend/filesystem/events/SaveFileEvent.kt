package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

class SaveFileEvent(val virtualDescriptor: VirtualDescriptor): InternalChangeEvent(FileChangeType.SAVE)