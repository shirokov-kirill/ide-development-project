package backend.filesystem

import java.nio.file.Path

open class FilesystemChangeEvent(val eventType: FileChangeType, val path: Path)