package backend.filesystem.events

class CreateFolderEvent(path: String, name: String): FilesystemChangeEvent(FileChangeType.CREATE_FOLDER)