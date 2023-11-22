package backend.filesystem.events

data class OpenProjectEvent(val absoluteProjectPath: String): FilesystemChangeEvent(FileChangeType.OPEN_PROJECT)