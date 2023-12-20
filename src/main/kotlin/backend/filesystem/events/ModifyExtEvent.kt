package backend.filesystem.events

class ModifyExtEvent(val realPath: String): ExternalChangeEvent(FileChangeType.EDIT_EXT)