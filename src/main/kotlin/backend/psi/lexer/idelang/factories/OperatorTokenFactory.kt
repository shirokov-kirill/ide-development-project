package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.OperatorToken

class OperatorTokenFactory(override val tokenCollection: MutableList<LexerToken>) : AbstractLexerTokenFactory() {
    override val regExpr = Regex("^(\\+|-|\\*|/|&&|==|=|%|>|<|!)")

    override fun appendToken(text: String) {
        tokenCollection.add(OperatorToken(text))
    }
}