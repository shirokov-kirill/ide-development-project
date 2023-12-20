package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

class CreateFolderEvent(val parent: VirtualDescriptor, val name: String): InternalChangeEvent(FileChangeType.CREATE_FOLDER)