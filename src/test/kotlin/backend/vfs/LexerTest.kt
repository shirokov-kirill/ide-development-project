package backend.vfs

import backend.psi.lexer.idelang.IDELangLexer
import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.tokens.TokenType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.*
import java.nio.charset.StandardCharsets
import kotlin.test.assertNull

class LexerTest {
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
        val tokens = lexer.process(File(localPath + "/testStructure/second.idl").readText())
        assertNull(tokens.firstOrNull { it.type == TokenType.UNDEFINED })
    }
}