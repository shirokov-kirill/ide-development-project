package backend.filesystem.events

open class InternalChangeEvent(eventType: FileChangeType): FilesystemChangeEvent(eventType)