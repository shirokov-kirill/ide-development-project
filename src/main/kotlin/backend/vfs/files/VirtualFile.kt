package backend.vfs.files

import java.nio.file.Path
import kotlin.io.path.Path

interface VirtualFile {
    val path: Path
    /*
     * Returns String for files
     * Returns null for folders
     */
    fun getFileContent(): String?

    fun insert(position: Int, text: String)

    fun delete(position: Int, count: Int)

    fun left()

    fun right()

    companion object {
        val Empty = object: VirtualFile {
            override val path: Path
                get() = Path("")

            override fun getFileContent(): String? {
                return null
            }

            override fun insert(position: Int, text: String) {
                return
            }

            override fun delete(position: Int, count: Int) {
                return
            }

            override fun left() {
                return
            }

            override fun right() {
                return
            }
        }
    }
}