package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.TokenType

class SeparatorTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^[ \n\r\t]")

    override fun appendToken(text: String) {
        tokenCollection.add(LexerToken(TokenType.SEPARATOR, text))
    }

}