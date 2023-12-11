package frontend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import frontend.caret.drawCaret

@Composable
fun App(textBuffer: List<Char>, caretPosition: Int) {
    val textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily.Monospace)

    MaterialTheme {
        val textLines = buildList {
            val currentLine = StringBuilder()
            textBuffer.forEach { char ->
                if (char == '\n') {
                    add(currentLine.toString())
                    currentLine.clear()
                } else {
                    currentLine.append(char)
                }
            }
            add(currentLine.toString())
        }

        Column(modifier = Modifier.fillMaxSize()) {
            textLines.forEach { line ->
                Text(
                    text = line, style = textStyle
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCaret(textLines, caretPosition, textStyle)
        }
    }
}