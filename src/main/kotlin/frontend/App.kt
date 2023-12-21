package frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import frontend.caret.drawCaret

@Composable
fun App(textBuffer: List<Char>, caretPosition: Int) {
    val textStyle = TextStyle(color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.Monospace)

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(darkGrey)) {
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

            val scrollState = rememberLazyListState()

            LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
                items(textLines) { line ->
                    Text(text = line, style = textStyle)
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                style = ScrollbarStyle(
                    thickness = 8.dp,
                    hoverDurationMillis = 300,
                    unhoverColor = mediumGrey,
                    minimalHeight = 16.dp,
                    shape = RoundedCornerShape(4.dp),
                    hoverColor = lightGrey
                ),
            )

            Canvas(modifier = Modifier.matchParentSize()) {
                drawCaret(textLines, caretPosition, textStyle)
            }
        }
    }
}
