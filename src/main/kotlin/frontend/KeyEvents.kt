package frontend

import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.utf16CodePoint
import frontend.caret.moveCaretLeft
import frontend.caret.moveCaretRight

@OptIn(ExperimentalComposeUiApi::class)
fun processKeyEvent(
    keyEvent: KeyEvent,
    caretPosition: MutableState<Int>,
    textBuffer: TextBuffer,
) {
    when {
        keyEvent.key == Key.Backspace && caretPosition.value > 0 -> {
            textBuffer.delete(caretPosition.value - 1)
            caretPosition.value--
        }

        keyEvent.key == Key.Delete && caretPosition.value < textBuffer.getSize() -> {
            textBuffer.delete(caretPosition.value)
        }

        keyEvent.key == Key.DirectionLeft -> {
            moveCaretLeft(caretPosition)
        }

        keyEvent.key == Key.DirectionRight -> {
            moveCaretRight(textBuffer, caretPosition)
        }

        keyEvent.key == Key.Enter -> {
            textBuffer.insert(caretPosition.value, '\n')
            caretPosition.value++
        }

        keyEvent.utf16CodePoint != 0 && keyEvent.key != Key.Backspace && keyEvent.key != Key.Delete && keyEvent.key != Key.Enter -> {
            textBuffer.insert(caretPosition.value, keyEvent.utf16CodePoint.toChar())
            caretPosition.value++
        }
    }
}