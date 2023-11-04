package backend.filesystem

interface VirtualFile {
    /*
     * Returns String for files
     * Returns null for folders
     */
    fun getFileContent(): String?

    fun modify(): Boolean
}