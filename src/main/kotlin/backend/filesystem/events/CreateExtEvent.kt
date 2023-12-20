package backend.filesystem.events

/*
 * Use only from FilesystemMonitor
 */
data class CreateExtEvent(val realPath: String): ExternalChangeEvent(FileChangeType.CREATE_EXT)