package backend.filesystem.events

/*
 * Use only from FilesystemMonitor
 */
data class CreateEvent(val realPath: String): FilesystemChangeEvent(FileChangeType.CREATE)