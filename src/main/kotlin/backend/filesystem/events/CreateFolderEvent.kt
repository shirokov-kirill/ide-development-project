package backend.filesystem.events

import backend.vfs.descriptors.VirtualDescriptor

class CreateFolderEvent(val parent: VirtualDescriptor, val name: String): FilesystemChangeEvent(FileChangeType.CREATE_FOLDER)