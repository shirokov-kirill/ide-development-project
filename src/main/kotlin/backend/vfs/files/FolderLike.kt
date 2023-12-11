package backend.vfs.files

import java.io.File
import java.nio.file.Path

class FolderLike(private val file: File): VirtualFile {
    override val path: Path = file.toPath()

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