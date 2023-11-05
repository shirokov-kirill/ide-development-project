package backend.filesystem

import backend.filesystem.structure.UpdatableFolderStructureTreeNode
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File


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

    @Test
    fun configFilesWriteNotFailOnExistingFiles1() {
        File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").mkdir()
        File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}").createNewFile()
        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").exists())
        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}").exists())
        fileManager.load("$localPath/testStructure")
        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").exists())
        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}").exists())
    }

    @Test
    fun configFilesWriteNotFailOnExistingFiles2() {
        File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").mkdir()
        val configFile = File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}")
        configFile.createNewFile()
        configFile.writeText("Hello world!")
        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").exists())
        assert(configFile.exists())
        assert(configFile.readLines().contains("Hello world!"))

        fileManager.load("$localPath/testStructure")

        assert(File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").exists())
        assert(configFile.exists())
        assert(!configFile.readLines().contains("Hello world!"))
        assert(configFile.readLines().contains("proj_dir: $localPath/testStructure"))
    }

    @AfterEach
    fun afterEach() {
        // delete tmp files
        File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}/${IDELangFileManager.projConfigFileName}").delete()
        File("$localPath/testStructure/${IDELangFileManager.projConfigFolderName}").delete()
    }
}