package backend.vfs

import backend.vfs.structure.UpdatableFolderStructureTreeNode
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
                .joinToString("/") + "/src/test/kotlin/backend/vfs"
        } else {
            null
        }
    }
    private val configDir = VfsManager.projConfigFolderName
    private val configFile = VfsManager.projConfigFileName

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
        assert(!File("$localPath/testStructure/${configDir}").exists())
        assert(!File("$localPath/testStructure/${configDir}/${configFile}").exists())
        fileManager.load("$localPath/testStructure")
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(File("$localPath/testStructure/${configDir}/${configFile}").exists())
    }

    @Test
    fun configFilesWriteNotFailOnExistingFiles1() {
        File("$localPath/testStructure/${configDir}").mkdir()
        File("$localPath/testStructure/${configDir}/${configFile}").createNewFile()
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(File("$localPath/testStructure/${configDir}/${configFile}").exists())
        fileManager.load("$localPath/testStructure")
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(File("$localPath/testStructure/${configDir}/${configFile}").exists())
    }

    @Test
    fun configFilesWriteNotFailOnExistingFiles2() {
        File("$localPath/testStructure/${configDir}").mkdir()
        val configFile = File("$localPath/testStructure/${configDir}/${configFile}")
        configFile.createNewFile()
        configFile.writeText("Hello world!")
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(configFile.exists())
        assert(configFile.readLines().contains("Hello world!"))

        fileManager.load("$localPath/testStructure")

        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(configFile.exists())
        assert(!configFile.readLines().contains("Hello world!"))
        assert(configFile.readLines().contains("proj_dir: $localPath/testStructure"))
    }

    @AfterEach
    fun afterEach() {
        // delete tmp files
        File("$localPath/testStructure/${configDir}/${configFile}").delete()
        File("$localPath/testStructure/${configDir}").delete()
    }
}