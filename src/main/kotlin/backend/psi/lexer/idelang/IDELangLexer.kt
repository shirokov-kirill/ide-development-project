package backend.psi.lexer.idelang

import androidx.compose.runtime.collectAsState
import backend.psi.PsiManager
import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.factories.*
import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.SpecialSToken
import backend.psi.lexer.idelang.tokens.UndefinedToken
import backend.vfs.FileManager
import backend.vfs.Vfs
import backend.vfs.descriptors.FileDescriptor
import backend.vfs.descriptors.VirtualDescriptorFileType
import backend.vfs.files.FileLike

class IDELangLexer(psiManager: PsiManager): Lexer {

    override val tokens: MutableMap<FileDescriptor, MutableList<LexerToken>> = mutableMapOf()

    private fun primary(fd: FileDescriptor): List<LexerTokenFactory> {
        // Throws IllegalStateException
        check(tokens.contains(fd))

        val tokenList = tokens[fd]!!
        return listOf(
            IdentifierTokenFactory(tokenList),
            SeparatorTokenFactory(tokenList),
            OperatorTokenFactory(tokenList),
            SpecialSTokenFactory(tokenList),
            StringTokenFactory(tokenList),
            NumberTokenFactory(tokenList)
        )
    }

    private fun secondary(fd: FileDescriptor): List<LexerTokenFactory> {
        // Throws IllegalStateException
        check(tokens.contains(fd))

        val tokenList = tokens[fd]!!
        return listOf(
            KeywordTokenFactory(tokenList),
            BooleanTokenFactory(tokenList),
            TypeTokenFactory(tokenList)
        )
    }

    private fun undefined(fd: FileDescriptor): UndefinedTokenFactory {
        // Throws IllegalStateException
        check(tokens.contains(fd))

        return UndefinedTokenFactory(tokens[fd]!!)
    }

    private val lexerWorkflow = Thread {
        while (true) {
            if (psiManager.updateState()) {
                for (data in psiManager.inputState){
                    check(data.key.getFile() is FileLike)
                    if(!tokens.contains(data.key)) {
                        tokens[data.key] = mutableListOf()
                    }
                    process(data.value, data.key) }
            }
            Thread.sleep(600)
        }
    }

    init {
        lexerWorkflow.start()
    }

    override fun process(text: String, fd: FileDescriptor) {
        var previousValue: CharSequence
        var currentValue: CharSequence = text
        val undefinedFactory = undefined(fd)
        while (currentValue.isNotEmpty()){
            previousValue = currentValue
            primary(fd).forEach {
                currentValue = it.matchOne(currentValue)
            }
            if(currentValue == previousValue) {
                currentValue = undefinedFactory.matchOne(currentValue)
            }
        }
        var index = 0
        val tokenCount = tokens.size
        while (index < tokenCount) {
            for(factory in secondary(fd)){
                if(factory.matchOneSecondary(index) > index)
                    break
            }
            index++
        }
    }
}