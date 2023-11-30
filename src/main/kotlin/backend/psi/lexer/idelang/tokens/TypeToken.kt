package backend.psi.lexer.idelang.tokens

class TypeToken(override val data: String) : LexerToken {
    override val type: TokenType = TokenType.TYPE
}