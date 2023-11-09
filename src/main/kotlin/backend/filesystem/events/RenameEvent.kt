package backend.filesystem.events

import java.nio.file.Path

data class RenameEvent(val path: Path, val newName: String): FilesystemChangeEvent(FileChangeType.RENAME)