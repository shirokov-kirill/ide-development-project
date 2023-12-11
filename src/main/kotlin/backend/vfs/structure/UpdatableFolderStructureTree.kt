package backend.vfs.structure

import backend.vfs.descriptors.FileDescriptor
import backend.vfs.descriptors.VirtualDescriptorFileType
import backend.vfs.descriptors.FolderDescriptor
import backend.vfs.descriptors.VirtualDescriptor
import backend.vfs.files.FileLike
import backend.vfs.files.FolderLike
import java.io.File

class UpdatableFolderStructureTree : UpdatableFolderStructure {
    var root: UpdatableFolderStructureTreeNode = UpdatableFolderStructureTreeNode.Empty
    private val descriptors: MutableMap<VirtualDescriptor, UpdatableFolderStructureTreeNode> = mutableMapOf()
    private val parents: MutableMap<UpdatableFolderStructureTreeNode, UpdatableFolderStructureTreeNode?> = mutableMapOf()

    override fun add(parent: VirtualDescriptor, item: VirtualDescriptor): Boolean {
        var currentNode: UpdatableFolderStructureTreeNode = descriptors[parent] ?: return false
        val newNode = UpdatableFolderStructureTreeNode(0, item, mutableListOf())
        newNode.hash = newNode.hashCode()

        currentNode.children.add(newNode)
        descriptors[item] = newNode;
        while (true) {
            currentNode.hash = currentNode.hashCode()
            currentNode = parents[currentNode] ?: return true
        }
    }

    override fun remove(item: VirtualDescriptor): Boolean {
        var currentNode: UpdatableFolderStructureTreeNode = descriptors[item] ?: return false

        if(currentNode == root) {
            descriptors.clear()
            parents.clear()
            root = UpdatableFolderStructureTreeNode.Empty
            parents[root] = null
            return true
        }

        val nodeToRemove = currentNode
        currentNode = parents[currentNode] as UpdatableFolderStructureTreeNode
        currentNode.children.remove(nodeToRemove)
        descriptors.remove(item)
        while (true) {
            currentNode.hash = currentNode.hashCode()
            currentNode = parents[currentNode] ?: return true
        }
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
            val files = file.listFiles()
            val descriptor = FolderDescriptor(file.name, FolderLike(file), VirtualDescriptorFileType.RootFolder)
            val node = UpdatableFolderStructureTreeNode(0, descriptor, mutableListOf())
            files?.let {
                for (file1 in it) {
                    load(file1, 1)?.let { it1 ->
                        parents[it1] = node
                        node.children.add(it1)
                    }
                }
            }
            node.hash = node.hashCode()
            descriptors[descriptor] = node
            return node
        } else if (file.exists() && file.isDirectory) {
            // load folder
            val files = file.listFiles()
            val descriptor = FolderDescriptor(file.name, FolderLike(file), VirtualDescriptorFileType.Folder)
            val node = UpdatableFolderStructureTreeNode(0, descriptor, mutableListOf())
            files?.let {
                for (file1 in it) {
                    load(file1, depth + 1)?.let { it1 ->
                        parents[it1] = node
                        node.children.add(it1)
                    }
                }
            }
            node.hash = node.hashCode()
            descriptors[descriptor] = node
            return node
        } else if (file.exists() && file.isFile && depth != 0) {
            // load file (not root)
            val descriptor = FileDescriptor(file.name, FileLike(file))
            val node = UpdatableFolderStructureTreeNode(0, descriptor, mutableListOf())
            val newHash = node.hashCode()
            node.hash = newHash
            descriptors[descriptor] = node
            return node
        } else {
            return null
        }
    }


}