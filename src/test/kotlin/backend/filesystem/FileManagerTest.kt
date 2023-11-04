package backend.filesystem

import backend.filesystem.structure.UpdatableFolderStructureTreeNode
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URLDecoder
import kotlin.test.assertIsNot


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

    @Test
    fun configFilesCreated() {
        assert(!File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").exists())
        assert(!File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}").exists())
        fileManager.load("$localPath/testStructure")
        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").exists())
        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}").exists())
    }

    @AfterEach
    fun afterEach() {
        // delete tmp files
        File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}").delete()
        File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").delete()
    }
}