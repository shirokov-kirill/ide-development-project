package frontend.files

import androidx.compose.runtime.MutableState
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.files.VirtualFile
import frontend.TextBuffer
import viewmodel.Filesystem

fun openFile(file: VirtualFile, textBuffer: TextBuffer, caretPosition: MutableState<Int>) {
    val content = file.getFileContent() ?: ""
    textBuffer.setText(content.toList())
    caretPosition.value = content.length
}

fun saveFile(filesystem: Filesystem, currentVirtualDescriptor: VirtualDescriptor, textBuffer: TextBuffer) {
    var currentFile = currentVirtualDescriptor.getFile()
    currentFile.getFileContent()?.let { currentFile.delete(0, it.length) }
    currentFile.insert(0, textBuffer.getText().joinToString(""))
    filesystem.saveFile(currentVirtualDescriptor)
}
