package backend.psi.lexer.idelang.tokens

class SpecialSToken(override val data: String): LexerToken {
    override val type: TokenType = TokenType.SPECIAL_SYMBOL
}