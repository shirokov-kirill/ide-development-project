package backend.filesystem.events

enum class FileChangeType {
    CREATE, // only for system listener
    CREATE_FILE,
    CREATE_FOLDER,
    EDIT,
    REMOVE,
    RENAME,
    CLOSE_PROJECT,
    OPEN_PROJECT,
}