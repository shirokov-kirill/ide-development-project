package backend.filesystem.events

data class OpenProjectEvent(val absoluteProjectPath: String): InternalChangeEvent(FileChangeType.OPEN_PROJECT)