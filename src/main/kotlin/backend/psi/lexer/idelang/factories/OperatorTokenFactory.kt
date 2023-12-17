package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.TokenType

class OperatorTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr = Regex("^(\\+|-|\\*|/|&&|==|=|%|>|<|!)")

    private val tokenTypeMap: Map<String, TokenType> = mapOf(
        "+" to TokenType.PLUS,
        "-" to TokenType.MINUS,
        "*" to TokenType.MULT,
        "/" to TokenType.DIV,
        "&&" to TokenType.AND,
        "==" to TokenType.EQUAL,
        "=" to TokenType.ASSIGN,
        "%" to TokenType.CONCAT,
        ">" to TokenType.MT,
        "<" to TokenType.LT,
        "!" to TokenType.NOT
    )

    override fun appendToken(text: String) {
        val type = tokenTypeMap[text] ?: throw IllegalStateException("Token should be present in the map")
        tokenCollection.add(LexerToken(type, text))
    }
}