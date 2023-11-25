package backend.psi.lexer.idelang.tokens

class SeparatorToken(override val data: String): LexerToken {
    override val type = TokenType.SEPARATOR
}