package backend.psi.lexer.idelang.tokens

class KeywordToken(override val data: String): LexerToken {
    override val type: TokenType = TokenType.KEYWORD
}