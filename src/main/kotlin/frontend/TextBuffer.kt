package frontend

class TextBuffer {
    private val text = mutableListOf<Char>()

    fun insert(position: Int, char: Char) {
        text.add(position, char)
    }

    fun delete(position: Int) {
        if (position in text.indices) {
            text.removeAt(position)
        }
    }

    fun getText(): List<Char> = text

    fun getSize(): Int = text.size
}
