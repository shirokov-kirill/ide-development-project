package backend.vfs

import backend.vfs.structure.UpdatableFolderStructureTreeNode
import kotlinx.coroutines.delay
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.assertNull
import kotlin.test.assertTrue


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
    private val configDir = Vfs.projConfigFolderName
    private val configFile = Vfs.projConfigFileName

    private var fileManager: FileManager = IDELangFileManager(null)

    @BeforeEach
    fun beforeEach() {
        fileManager = IDELangFileManager(null)
    }

    @Test
    fun loadProject01Test() {
        val fileTree = fileManager.folderTree
        assert(fileTree.value == UpdatableFolderStructureTreeNode.Empty)
        fileManager.load("$localPath/testStructure")
        Thread.sleep(5000)
        assert(fileTree.value != UpdatableFolderStructureTreeNode.Empty)
    }

    @Test
    fun loadProject02() {
        assert(!File("$localPath/testStructure/${configDir}").exists())
        assert(!File("$localPath/testStructure/${configDir}/${configFile}").exists())
        fileManager.load("$localPath/testStructure")
        Thread.sleep(3000)
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(File("$localPath/testStructure/${configDir}/${configFile}").exists())
    }

    @Test
    fun loadProject03() {
        File("$localPath/testStructure/${configDir}").mkdir()
        File("$localPath/testStructure/${configDir}/${configFile}").createNewFile()
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(File("$localPath/testStructure/${configDir}/${configFile}").exists())
        fileManager.load("$localPath/testStructure")
        Thread.sleep(1000)
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(File("$localPath/testStructure/${configDir}/${configFile}").exists())
    }

    @Test
    fun loadProject04() {
        File("$localPath/testStructure/${configDir}").mkdir()
        val configFile = File("$localPath/testStructure/${configDir}/${configFile}")
        configFile.createNewFile()
        configFile.writeText("Hello world!")
        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(configFile.exists())
        assert(configFile.readLines().contains("Hello world!"))

        fileManager.load("$localPath/testStructure")
        Thread.sleep(1000)

        assert(File("$localPath/testStructure/${configDir}").exists())
        assert(configFile.exists())
        val lines = configFile.readLines()
        assert(!lines.contains("Hello world!"))
        assert(configFile.readLines().contains("proj_dir: $localPath/testStructure"))
    }

    @Test
    fun remove01Test() {
        fileManager.load("$localPath/testStructure")
        Thread.sleep(1000)
        val descriptor = fileManager.folderTree.value.children.first { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove.idl" }.virtualDescriptor
        fileManager.delete(descriptor)
        Thread.sleep(2000)
        val childrenCount = fileManager.folderTree.value.children.count()
        File("$localPath/testStructure/ToRemove.idl").createNewFile()
        assertTrue( childrenCount == 5)
    }

    @Test
    fun remove02Test() {
        fileManager.load("$localPath/testStructure")
        Thread.sleep(1000)
        val descriptor = fileManager.folderTree.value.children.first { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove.idl" }.virtualDescriptor
        val descriptor2 = fileManager.folderTree.value.children.first { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove2.idl" }.virtualDescriptor
        val descriptor3 = fileManager.folderTree.value.children.first { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove3.idl" }.virtualDescriptor
        fileManager.delete(descriptor)
        Thread.sleep(2000)
        assertTrue( fileManager.folderTree.value.children.count() == 5)
        assertNull(fileManager.folderTree.value.children.firstOrNull { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove.idl" })
        fileManager.delete(descriptor2)
        Thread.sleep(2000)
        assertTrue( fileManager.folderTree.value.children.count() == 4)
        assertNull(fileManager.folderTree.value.children.firstOrNull { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove2.idl" })
        fileManager.delete(descriptor3)
        Thread.sleep(2000)
        assertTrue( fileManager.folderTree.value.children.count() == 3)
        assertNull(fileManager.folderTree.value.children.firstOrNull { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove3.idl" })
        Thread.sleep(2000)
        File("$localPath/testStructure/ToRemove.idl").createNewFile()
        File("$localPath/testStructure/ToRemove2.idl").createNewFile()
        File("$localPath/testStructure/ToRemove3.idl").createNewFile()
    }

    @Test
    fun remove03Test() {
        fileManager.load("$localPath/testStructure")
        Thread.sleep(1000)
        val descriptor = fileManager.folderTree.value.children.first { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove.idl" }.virtualDescriptor
        val descriptor2 = fileManager.folderTree.value.children.first { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove2.idl" }.virtualDescriptor
        val descriptor3 = fileManager.folderTree.value.children.first { (it as UpdatableFolderStructureTreeNode).fileName == "ToRemove3.idl" }.virtualDescriptor
        fileManager.delete(descriptor)
        fileManager.delete(descriptor2)
        fileManager.delete(descriptor3)
        Thread.sleep(3000)
        assertTrue( fileManager.folderTree.value.children.count() == 3)
        File("$localPath/testStructure/ToRemove.idl").createNewFile()
        File("$localPath/testStructure/ToRemove2.idl").createNewFile()
        File("$localPath/testStructure/ToRemove3.idl").createNewFile()
    }

    @Test
    fun create01Test() {
        fileManager.load("$localPath/testStructure")
        Thread.sleep(1000)
        fileManager.create(fileManager.folderTree.value.virtualDescriptor, "temporary1.txt", FileManager.CREATE_FILE)
        Thread.sleep(3000)
        assertTrue( fileManager.folderTree.value.children.count() == 7)
        File("$localPath/testStructure/temporary1.txt").deleteRecursively()
    }

    @Test
    fun create02Test() {
        fileManager.load("$localPath/testStructure")
        Thread.sleep(1000)
        fileManager.create(fileManager.folderTree.value.virtualDescriptor, "temporary1.txt", FileManager.CREATE_DIR)
        Thread.sleep(3000)
        assertTrue( fileManager.folderTree.value.children.count() == 7)
        File("$localPath/testStructure/temporary1.txt").deleteRecursively()
    }

    @AfterEach
    fun afterEach() {
        // delete tmp files
        File("$localPath/testStructure/${configDir}/${configFile}").delete()
        File("$localPath/testStructure/${configDir}").delete()
    }
}