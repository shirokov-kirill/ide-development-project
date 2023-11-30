package backend.psi.lexer.idelang.tokens

class StringToken(override val data: String) : LexerToken {
    override val type: TokenType = TokenType.STRING
}