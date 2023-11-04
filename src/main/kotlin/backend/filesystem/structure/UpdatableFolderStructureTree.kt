package backend.filesystem.structure

import backend.filesystem.VirtualDescriptorFileType
import backend.filesystem.descriptors.FolderDescriptor
import backend.filesystem.descriptors.VirtualDescriptor
import java.io.File

class UpdatableFolderStructureTree : UpdatableFolderStructure {
    private var root: UpdatableFolderStructureTreeNode = UpdatableFolderStructureTreeNode.Empty
    private var filePath: String = ""

    override fun add(item: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(item: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    override fun load(filePath: String): FolderStructureNode {
        this.filePath = filePath
        load(File(filePath), 0)?. let {
            root = it
        }
        return root
    }

    override fun loadSecondaryAndUpdate(): FolderStructureNode {
        var node: UpdatableFolderStructureTreeNode = UpdatableFolderStructureTreeNode.Empty
        load(File(filePath), 0)?. let {
            node = it
        }
        if(node.hash != root.hash) {
            root = node
        }
        return root
    }

    override fun isEmpty(): Boolean {
        return root.virtualDescriptor.type == VirtualDescriptorFileType.Empty
    }

    private fun load(file: File, depth: Int): UpdatableFolderStructureTreeNode? {
        if(file.exists() && file.isDirectory && depth == 0) {
            // load root folder
            val children = mutableListOf<FolderStructureNode>()
            val files = file.listFiles()
            files?.let {
                for (file1 in it) {
                    load(file1, 1)?.let { it1 ->
                        children.add(it1)
                    }
                }
            }
            val node = UpdatableFolderStructureTreeNode(0, FolderDescriptor(file.name, file.path, VirtualDescriptorFileType.RootFolder), children)
            node.hash = node.hashCode()
            return node
        } else if (file.exists() && file.isDirectory) {
            // load folder
            val children = mutableListOf<FolderStructureNode>()
            val files = file.listFiles()
            files?.let {
                for (file1 in it) {
                    load(file1, depth + 1)?.let { it1 ->
                        children.add(it1)
                    }
                }
            }
            val node = UpdatableFolderStructureTreeNode(0, FolderDescriptor(file.name, file.path, VirtualDescriptorFileType.Folder), children)
            node.hash = node.hashCode()
            return node
        } else if (file.exists() && file.isFile) {
            // load file
            val node = UpdatableFolderStructureTreeNode(0, FolderDescriptor(file.name, file.path, VirtualDescriptorFileType.File), listOf())
            node.hash = node.hashCode()
            return node
        } else {
            return null
        }
    }


}