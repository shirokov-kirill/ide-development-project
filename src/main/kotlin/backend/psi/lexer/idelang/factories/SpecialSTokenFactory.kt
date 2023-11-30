package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.SeparatorToken
import backend.psi.lexer.idelang.tokens.SpecialSToken

class SpecialSTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("^[,(){};:]")

    override fun appendToken(text: String) {
        tokenCollection.add(SpecialSToken(text))
    }

}