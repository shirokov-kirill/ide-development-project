package backend.psi.lexer.idelang.tokens

class IdentifierToken(override val data: String) : LexerToken {
    override val type = TokenType.IDENTIFIER
}