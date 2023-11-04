package backend.filesystem

import backend.filesystem.structure.UpdatableFolderStructureTreeNode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URLDecoder


class FileManagerTest {
    private val localPath = let {
        val classLoader = ClassLoader.getSystemClassLoader()
        val resourcePath = FileManagerTest::class.qualifiedName?.replace(".", "/") + ".class"
        val resource = classLoader.getResource(resourcePath)
        if(resource != null) {
            File(resource.toURI()).absolutePath
                .split("\\")
                .reversed()
                .dropWhile { it != "ide-development-project" }
                .reversed()
                .joinToString("/") + "/src/test/kotlin/backend/filesystem"
        } else {
            null
        }
    }

    private var fileManager: FileManager = IDELangFileManager()

    @BeforeEach
    fun beforeEach() {
        fileManager = IDELangFileManager()
    }

    @Test
    fun fileManagerMutableViewTest() {
        val fileTree = fileManager.folderTree
        assert(fileTree.value == UpdatableFolderStructureTreeNode.Empty)
        fileManager.load("$localPath/testStructure")
        assert(fileTree.value != UpdatableFolderStructureTreeNode.Empty)
    }
}