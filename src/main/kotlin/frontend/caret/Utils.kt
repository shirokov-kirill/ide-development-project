package frontend.caret

fun getCaretXPosition(text: List<Char>, caretPosition: Int): Int {
    return caretPosition - text.take(caretPosition).joinToString("").lastIndexOf('\n') - 1
}