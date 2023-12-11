package backend.psi.lexer

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.vfs.descriptors.FileDescriptor

interface Lexer {
    val tokens: MutableMap<FileDescriptor, MutableList<LexerToken>>

    fun process(text: String, fd: FileDescriptor)
}