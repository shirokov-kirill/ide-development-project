package backend.vfs.files

import backend.vfs.files.structure.GapBuffer
import java.io.File
import java.nio.file.Path

class FileLike(private val file: File): VirtualFile {
    override val path: Path = file.toPath()
    private val structure: GapBuffer = GapBuffer(CharArray(100){ ' ' } + file.readText().toCharArray(), 0, 100)

    override fun getFileContent(): String? {
        return structure.toString()
    }

    override fun insert(position: Int, text: String) {
        structure.insert(text, position)
    }

    override fun delete(position: Int, count: Int) {
        structure.delete(position, position + count)
    }

    override fun left() {
        structure.left()
    }

    override fun right() {
        structure.right()
    }
}