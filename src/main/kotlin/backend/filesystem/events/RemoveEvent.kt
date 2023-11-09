package backend.filesystem.events

data class RemoveEvent(val filePath: String): FilesystemChangeEvent(FileChangeType.REMOVE)