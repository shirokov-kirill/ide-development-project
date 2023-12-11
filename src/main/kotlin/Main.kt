import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import frontend.App
import frontend.TextBuffer
import frontend.caret.getCaretXPosition
import frontend.caret.moveCaretDown
import frontend.caret.moveCaretUp
import frontend.processKeyEvent

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val textBuffer = TextBuffer()
    val caretPosition = remember { mutableStateOf(0) }
    var initialXPosition: Int? = null

    Window(onCloseRequest = ::exitApplication, onKeyEvent = { keyEvent ->
        if (keyEvent.type == KeyEventType.KeyDown) {
            when (keyEvent.key) {
                Key.DirectionUp -> {
                    if (initialXPosition == null) {
                        initialXPosition = getCaretXPosition(textBuffer.getText(), caretPosition.value)
                    }
                    moveCaretUp(textBuffer, caretPosition, initialXPosition)
                }

                Key.DirectionDown -> {
                    if (initialXPosition == null) {
                        initialXPosition = getCaretXPosition(textBuffer.getText(), caretPosition.value)
                    }
                    moveCaretDown(textBuffer, caretPosition, initialXPosition)
                }

                else -> {
                    initialXPosition = null
                    processKeyEvent(keyEvent, caretPosition, textBuffer)
                }
            }
        }
        true
    }) {
        App(textBuffer = textBuffer.getText(), caretPosition = caretPosition.value)
    }
}
