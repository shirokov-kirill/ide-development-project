package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.TokenType

class SpecialSTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^[,(){};:]")

    private val tokenTypeMap: Map<String, TokenType> = mapOf(
        "," to TokenType.COMMA,
        "(" to TokenType.LBRACE,
        ")" to TokenType.RBRACE,
        "{" to TokenType.CURLY_LBRACE,
        "}" to TokenType.CURLY_RBRACE,
        ";" to TokenType.SEMICOLON,
        ":" to TokenType.DOUBLEDOT,
    )

    override fun appendToken(text: String) {
        val type = tokenTypeMap[text] ?: throw IllegalStateException("Token should be present in the map")
        tokenCollection.add(LexerToken(type, text))
    }

}