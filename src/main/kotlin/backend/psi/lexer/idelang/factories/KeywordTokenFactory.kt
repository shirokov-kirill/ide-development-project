package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.IdentifierToken
import backend.psi.lexer.idelang.tokens.KeywordToken
import backend.psi.lexer.idelang.tokens.LexerToken

class KeywordTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^(var|proc|return|func|while|if|else)")

    override fun appendToken(text: String) {}

    override fun matchOneSecondary(position: Int): Int {
        val token = tokenCollection[position]
        if(token is IdentifierToken) {
            val hasMatch = regExpr.find(token.data, 0)
            hasMatch?. let {
                if(it.range.last + 1 == token.data.length) {
                    tokenCollection[position] = KeywordToken(token.data)
                }
                return position + 1
            }
        }
        return position
    }

}