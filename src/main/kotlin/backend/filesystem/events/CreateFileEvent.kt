package backend.filesystem.events

data class CreateFileEvent(val path: String, val fileName: String): FilesystemChangeEvent(FileChangeType.CREATE_FILE)