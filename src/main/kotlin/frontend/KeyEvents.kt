package frontend

import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import frontend.caret.moveCaretLeft
import frontend.caret.moveCaretRight

@OptIn(ExperimentalComposeUiApi::class)
val exclusions = setOf(Key.Backspace, Key.Delete, Key.Enter, Key.ShiftLeft, Key.ShiftRight, Key.CtrlLeft, Key.CtrlRight, Key.MetaLeft, Key.MetaRight, Key.CapsLock)

@OptIn(ExperimentalComposeUiApi::class)
fun processKeyEvent(
    keyEvent: KeyEvent,
    caretPosition: MutableState<Int>,
    textBuffer: TextBuffer,
    isMetaPressed: Boolean,
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
            moveCaretLeft(textBuffer, caretPosition, isMetaPressed)
        }

        keyEvent.key == Key.DirectionRight -> {
            moveCaretRight(textBuffer, caretPosition, isMetaPressed)
        }

        keyEvent.key == Key.Enter -> {
            textBuffer.insert(caretPosition.value, '\n')
            caretPosition.value++
        }

        keyEvent.utf16CodePoint != 0 && keyEvent.key !in exclusions -> {
            textBuffer.insert(caretPosition.value, keyEvent.utf16CodePoint.toChar())
            caretPosition.value++
        }
    }
}
