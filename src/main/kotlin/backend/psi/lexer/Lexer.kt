package backend.psi.lexer

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.vfs.descriptors.FileDescriptor

interface Lexer {
    fun process(text: String): List<LexerToken>
}