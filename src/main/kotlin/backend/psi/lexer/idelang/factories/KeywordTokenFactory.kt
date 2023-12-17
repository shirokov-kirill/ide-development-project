package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.TokenType

class KeywordTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^(var|proc|return|func|while|if|else)")

    private val tokenTypeMap: Map<String, TokenType> = mapOf(
        "var" to TokenType.VAR,
        "proc" to TokenType.PROC,
        "return" to TokenType.RETURN,
        "func" to TokenType.FUNC,
        "while" to TokenType.WHILE,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
    )

    override fun appendToken(text: String) {}

    override fun matchOneSecondary(position: Int): Int {
        val token = tokenCollection[position]
        if(token.type == TokenType.IDENTIFIER) {
            val hasMatch = regExpr.find(token.data, 0)
            hasMatch?. let {
                if(it.range.last + 1 == token.data.length) {
                    val type = tokenTypeMap[token.data] ?: throw IllegalStateException("Token should be present in the map")
                    tokenCollection[position] = LexerToken(type, token.data)
                }
                return position + 1
            }
        }
        return position
    }

}