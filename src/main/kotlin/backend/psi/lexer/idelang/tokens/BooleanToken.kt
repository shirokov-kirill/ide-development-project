package backend.psi.lexer.idelang.tokens

class BooleanToken(override val data: String) : LexerToken {
    override val type: TokenType = TokenType.BOOLEAN
}