package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.TokenType

class UndefinedTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr: Regex = Regex("")

    override fun matchOne(input: CharSequence): CharSequence {
        appendToken(input[0].toString())
        return input.drop(1)
    }

    override fun appendToken(text: String) {
        tokenCollection.add(LexerToken(TokenType.UNDEFINED, text))
    }

}