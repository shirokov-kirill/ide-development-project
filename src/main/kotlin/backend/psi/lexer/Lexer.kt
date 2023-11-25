package backend.psi.lexer

import backend.psi.lexer.idelang.tokens.LexerToken

interface Lexer {
    val tokens: MutableList<LexerToken>

    fun process(text: String)
}