package backend.vfs.structure

import backend.vfs.descriptors.VirtualDescriptorFileType
import backend.vfs.descriptors.FolderDescriptor
import backend.vfs.descriptors.VirtualDescriptor
import java.io.File

class UpdatableFolderStructureTree : UpdatableFolderStructure {
    var root: UpdatableFolderStructureTreeNode = UpdatableFolderStructureTreeNode.Empty

    override fun add(item: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(item: VirtualDescriptor): Boolean {
        TODO("Not yet implemented")
    }

    /*
     * filePath - path to the folder to load
     */
    override fun load(filePath: String): FolderStructureNode {
        load(File(filePath), 0)?. let {
            root = it
        }
        return root
    }

    override fun loadSecondaryAndUpdate(filePath: String): FolderStructureNode {
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
            val node = UpdatableFolderStructureTreeNode(0, FolderDescriptor(file.name, VirtualDescriptorFileType.RootFolder), children)
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
            val node = UpdatableFolderStructureTreeNode(0, FolderDescriptor(file.name, VirtualDescriptorFileType.Folder), children)
            node.hash = node.hashCode()
            return node
        } else if (file.exists() && file.isFile && depth != 0) {
            // load file (not root)
            val node = UpdatableFolderStructureTreeNode(0, FolderDescriptor(file.name, VirtualDescriptorFileType.File), listOf())
            node.hash = node.hashCode()
            return node
        } else {
            return null
        }
    }


}