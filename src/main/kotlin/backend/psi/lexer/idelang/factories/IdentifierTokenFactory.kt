package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.IdentifierToken
import backend.psi.lexer.idelang.tokens.LexerToken

class IdentifierTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr = Regex("^[A-Za-z_][A-Za-z_0-9]*")

    override fun appendToken(text: String) {
        tokenCollection.add(IdentifierToken(text))
    }
}