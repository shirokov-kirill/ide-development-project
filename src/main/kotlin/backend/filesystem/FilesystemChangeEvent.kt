package backend.filesystem

import java.nio.file.Path
import java.nio.file.WatchEvent

data class FilesystemChangeEvent(val eventType: FileChangeType, val path: Path)