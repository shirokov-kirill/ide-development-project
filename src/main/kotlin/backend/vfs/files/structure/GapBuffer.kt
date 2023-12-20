package backend.vfs.files.structure

import java.util.Arrays

class GapBuffer(initBuffer: CharArray, initGapStart: Int = 0, initGapEnd: Int = 100) {

    private var capacity = initBuffer.size

    private var buffer = initBuffer

    private var gapStart = initGapStart

    private var gapEnd = initGapEnd

    private fun gapLength(): Int = gapEnd - gapStart

    operator fun get(index: Int): Char {
        return if (index < gapStart) {
            buffer[index]
        } else {
            buffer[index - gapStart + gapEnd]
        }
    }

    private fun makeSureAvailableSpace(requestSize: Int) {
        if (requestSize <= gapLength()) {
            return
        }

        // Allocating necessary memory space by doubling the array size.
        val necessarySpace = requestSize - gapLength()
        var newCapacity = capacity * 2
        while ((newCapacity - capacity) < necessarySpace) {
            newCapacity *= 2
        }

        val newBuffer = CharArray(newCapacity)
        buffer.copyInto(newBuffer, 0, 0, gapStart)
        val tailLength = capacity - gapEnd
        val newEnd = newCapacity - tailLength
        buffer.copyInto(newBuffer, newEnd, gapEnd, gapEnd + tailLength)

        buffer = newBuffer
        capacity = newCapacity
        gapEnd = newEnd
    }

    fun delete(start: Int, end: Int) {
        if (start < gapStart && end <= gapStart) {
            // remove in the beginning
            val copyLen = gapStart - end
            buffer.copyInto(buffer, gapEnd - copyLen, end, gapStart)
            gapStart = start
            gapEnd -= copyLen
        } else if (start < gapStart) {
            // remove on the gap
            gapEnd = end + gapLength()
            gapStart = start
        } else {
            // remove in the end
            val startInBuffer = start + gapLength()
            val endInBuffer = end + gapLength()
            val copyLen = startInBuffer - gapEnd
            buffer.copyInto(buffer, gapStart, gapEnd, startInBuffer)
            gapStart += copyLen
            gapEnd = endInBuffer
        }
    }

    fun insert(text: String, start: Int = gapStart) {
        makeSureAvailableSpace(text.length)
        text.toCharArray(buffer, gapStart)
        gapStart += text.length
    }

    fun left() {
        buffer[gapEnd - 1] = buffer[gapStart - 1]
        gapEnd -= 1
        gapStart -= 1
    }

    fun right() {
        buffer[gapStart] = buffer[gapEnd]
        gapEnd += 1
        gapStart += 1
    }

    fun moveFor(number: Int) {
        if(number < 0) {
            repeat(number) {
                left()
            }
        } else {
            repeat(number) {
                right()
            }
        }
    }

    private fun append(builder: StringBuilder): StringBuilder {
        builder.append(buffer, 0, gapStart)
        builder.append(buffer, gapEnd, capacity - gapEnd)
        return builder
    }

    fun length() = capacity - gapLength()

    override fun toString(): String = append(StringBuilder()).toString()
}