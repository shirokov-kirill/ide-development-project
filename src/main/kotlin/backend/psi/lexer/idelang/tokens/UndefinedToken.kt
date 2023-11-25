package backend.psi.lexer.idelang.tokens

class UndefinedToken(override val data: String) : LexerToken {
    override val type = TokenType.UNDEFINED
}