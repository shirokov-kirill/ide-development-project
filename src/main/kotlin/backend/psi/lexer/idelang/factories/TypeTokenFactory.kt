package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.*

class TypeTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^(boolean|string|number)")

    override fun appendToken(text: String) {}

    override fun matchOneSecondary(position: Int): Int {
        val token = tokenCollection[position]
        if(token.type == TokenType.IDENTIFIER) {
            val hasMatch = regExpr.find(token.data, 0)
            hasMatch?. let {
                if(it.range.last + 1 == token.data.length) {
                    tokenCollection[position] = LexerToken(TokenType.TYPE, token.data)
                }
                return position + 1
            }
        }
        return position
    }

}