package backend.psi.lexer.idelang.tokens

interface LexerToken {
    val type: TokenType
    val data: String
}