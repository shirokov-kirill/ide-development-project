package backend.psi.lexer.idelang.tokens

class NumberToken(override val data: String) : LexerToken {
    override val type: TokenType = TokenType.NUMBER
}