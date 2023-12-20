package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

class DeleteExtEvent(val realPath: String): ExternalChangeEvent(FileChangeType.REMOVE_EXT)