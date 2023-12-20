package backend.vfs.structure

interface UpdatableFolderStructure : FolderStructure {
    fun loadSecondaryAndUpdate(filePath: String): UpdatableFolderStructureTreeNode
}