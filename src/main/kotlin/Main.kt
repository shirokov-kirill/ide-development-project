import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Divider
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import backend.vfs.IDELangFileManager
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.files.VirtualFile
import frontend.caret.getCaretXPosition
import frontend.caret.moveCaretDown
import frontend.caret.moveCaretUp
import frontend.files.fileBrowser
import frontend.files.openFile
import frontend.files.saveFile
import viewmodel.Filesystem
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.material.MaterialTheme
import frontend.*


@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val textBuffer = TextBuffer()
    val caretPosition = remember { mutableStateOf(0) }
    var initialXPosition: Int? = null
    var currentFile: VirtualFile? by remember { mutableStateOf(VirtualFile.Empty) }
    var currentVirtualDescriptor: VirtualDescriptor? by remember { mutableStateOf(VirtualDescriptor.Empty) }
    val filesystem = Filesystem(IDELangFileManager(null))
    filesystem.openFolder("/Users/excuseem/IdeaProjects/ide-development-project/New Project")

    MaterialTheme {
        Window(onCloseRequest = ::exitApplication, onKeyEvent = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                val isMetaPressed = keyEvent.isMetaPressed
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
                        processKeyEvent(keyEvent, caretPosition, textBuffer, isMetaPressed)
                    }
                }
            }
            true
        }) {
            Box(Modifier.fillMaxSize().background(darkGrey)) {
                val focusManager = LocalFocusManager.current
                var selectedFileContent by remember { mutableStateOf<String?>(null) }

                Row(Modifier.align(Alignment.TopStart).background(mediumGrey)) {
                    fileBrowser(filesystem) { virtualFile ->
                        currentVirtualDescriptor = virtualFile
                        currentFile = virtualFile.getFile()
                        selectedFileContent = currentFile!!.getFileContent()
                        openFile(currentFile!!, textBuffer, caretPosition)
                        focusManager.clearFocus()
                    }

                    Divider(
                        color = mediumGrey,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Box(Modifier.background(darkGrey)) {
                        App(
                            textBuffer = textBuffer.getText(),
                            caretPosition = caretPosition.value,
                        )
                    }
                }

                Button(
                    onClick = {
                        currentVirtualDescriptor?.let { saveFile(filesystem, it, textBuffer) }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = accentColor),
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                ) {
                    Text("Save", fontFamily = FontFamily.SansSerif, fontSize = 16.sp, color = Color.Black)
                }
            }
        }
    }

}
