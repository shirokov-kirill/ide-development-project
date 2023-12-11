package frontend.caret

import androidx.compose.runtime.MutableState
import frontend.TextBuffer
import java.lang.Integer.min

fun moveCaretLeft(caretPosition: MutableState<Int>) {
    caretPosition.value = (caretPosition.value - 1).coerceAtLeast(0)
}

fun moveCaretRight(textBuffer: TextBuffer, caretPosition: MutableState<Int>) {
    caretPosition.value = (caretPosition.value + 1).coerceAtMost(textBuffer.getSize())
}

fun moveCaretUp(textBuffer: TextBuffer, caretPosition: MutableState<Int>, initialXPosition: Int?) {
    val text = textBuffer.getText()

    val currentPosition = caretPosition.value
    val currentLineStart = text.take(currentPosition).joinToString("").lastIndexOf('\n') + 1

    val previousLineEnd = text.take(currentLineStart).joinToString("").lastIndexOf('\n')
    val previousLineStart = if (previousLineEnd == -1) 0 else
        text.take(previousLineEnd).joinToString("").lastIndexOf('\n') + 1

    if (previousLineStart == currentLineStart) {
        caretPosition.value = 0
    } else {
        caretPosition.value = min(previousLineStart + initialXPosition!!, previousLineEnd)
    }
}

fun moveCaretDown(textBuffer: TextBuffer, caretPosition: MutableState<Int>, initialXPosition: Int?) {
    val text = textBuffer.getText().joinToString("")

    val currentPosition = caretPosition.value
    val currentLineEnd = text.substring(currentPosition).indexOf('\n').let {
        if (it == -1) text.length else it + currentPosition
    }

    val nextLineStart = if (currentLineEnd >= text.length) text.length else currentLineEnd + 1
    val nextLineEnd = text.substring(nextLineStart).indexOf('\n').let {
        if (it == -1) text.length else it + nextLineStart
    }

    if (currentLineEnd == nextLineEnd) {
        caretPosition.value = text.length
    }

    caretPosition.value = min(nextLineStart + initialXPosition!!, nextLineEnd)
}
