package backend.psi.lexer.idelang.tokens

class OperatorToken(override val data: String) : LexerToken {
    override val type: TokenType = TokenType.OPERATOR
}