package frontend.caret

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle

fun DrawScope.drawCaret(textLines: List<String>, caretPosition: Int, textStyle: TextStyle) {
    var accumulatedCharCount = 0
    var lineIndex = 0
    val estimatedCharWidth = textStyle.fontSize.toPx() * 0.6f // Estimate average char width

    val lineHeight = textStyle.fontSize.toPx() // Adjusted for line spacing

    for (line in textLines) {
        if (caretPosition <= accumulatedCharCount + line.length) {
            val charsBeforeCaret = caretPosition - accumulatedCharCount
            val caretX = charsBeforeCaret * estimatedCharWidth // Adjusted horizontal position
            val caretY = lineIndex * lineHeight

            drawLine(
                color = Color.Black,
                start = Offset(caretX, caretY),
                end = Offset(caretX, caretY + lineHeight),
                strokeWidth = 2f // Adjust caret width here
            )
            break
        }
        accumulatedCharCount += line.length + 1
        lineIndex++
    }
}
