package backend.filesystem.events

open class ExternalChangeEvent(eventType: FileChangeType): FilesystemChangeEvent(eventType)