package frontend.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import backend.vfs.descriptors.FileDescriptor
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.structure.FolderStructureNode
import viewmodel.Filesystem

@Composable
fun fileBrowser(filesystem: Filesystem, onFileSelected: (VirtualDescriptor) -> Unit) {
    val folderStructure by filesystem.virtualFolderStructure.collectAsState()

    displayFolderStructure(folderStructure, onFileSelected)
}

@Composable
fun displayFolderStructure(node: FolderStructureNode, onFileSelected: (VirtualDescriptor) -> Unit, indentLevel: Int = 0) {
    val indentation = 10.dp * indentLevel

    Column(modifier = Modifier.padding(start = indentation)) {
        Text(text = node.virtualDescriptor.name, style = TextStyle(color = Color.White))

        node.children.forEach { child ->
            if (child.virtualDescriptor is FileDescriptor) {
                Text(
                    text = child.virtualDescriptor.name,
                    style = TextStyle(color = Color.White),
                    modifier = Modifier
                        .clickable { onFileSelected(child.virtualDescriptor) }
                        .padding(start = indentation + 10.dp)
                )
            } else {
                displayFolderStructure(child, onFileSelected, indentLevel + 1)
            }
        }
    }
}
