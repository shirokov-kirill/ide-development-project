package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.NumberToken

class NumberTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^[0-9]+")

    override fun appendToken(text: String) {
        tokenCollection.add(NumberToken(text))
    }

}