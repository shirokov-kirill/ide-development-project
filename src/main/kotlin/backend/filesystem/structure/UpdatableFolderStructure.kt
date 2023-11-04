package backend.filesystem.structure

interface UpdatableFolderStructure : FolderStructure {
    fun loadSecondaryAndUpdate(): FolderStructureNode
}