package backend.filesystem

enum class FileChangeType {
    SYSTEM_CREATE,
    SYSTEM_EDIT,
    SYSTEM_REMOVE,
    LOCAL_CREATE,
    LOCAL_EDIT,
    LOCAL_REMOVE
}