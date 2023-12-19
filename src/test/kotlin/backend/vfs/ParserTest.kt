package backend.vfs

import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.IDELangLexer
import backend.psi.lexer.idelang.tokens.TokenType
import backend.psi.parser.idelang.IDELangParser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertNull

class ParserTest {
    private var baos = ByteArrayOutputStream()
    private val out = System.out

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

    private var lexer: Lexer = IDELangLexer()

    @BeforeEach
    fun beforeEach() {
        baos.reset()
        System.setOut(PrintStream(baos))
    }

    @AfterEach
    fun afterEach() {
        System.setOut(out)
        baos.reset()
    }

    @Test
    fun test01() {
        val tokens = lexer.process(File(localPath + "/testStructure/third.idl").readText())
        val parser = IDELangParser(tokens)
        val result = parser.parse()
        assertNull(result.exception)
    }
}