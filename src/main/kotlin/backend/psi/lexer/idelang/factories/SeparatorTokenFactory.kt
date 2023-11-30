package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.SeparatorToken

class SeparatorTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^[ \n\r\t]")

    override fun appendToken(text: String) {
        tokenCollection.add(SeparatorToken(text))
    }

}