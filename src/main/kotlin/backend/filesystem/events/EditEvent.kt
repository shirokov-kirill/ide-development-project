package backend.filesystem.events

/*
 * Use only from FilesystemMonitor
 */
data class EditEvent(val realPath: String): InternalChangeEvent(FileChangeType.EDIT)