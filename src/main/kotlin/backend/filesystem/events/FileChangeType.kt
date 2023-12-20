package backend.filesystem.events

enum class FileChangeType {
    CREATE_EXT,
    CREATE_FILE,
    CREATE_FOLDER,
    EDIT_EXT,
    EDIT,
    REMOVE_EXT,
    REMOVE,
    RENAME,
    CLOSE_PROJECT,
    OPEN_PROJECT,
    SAVE
}