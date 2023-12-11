package backend.psi.lexer.idelang.factories

import backend.psi.lexer.idelang.tokens.LexerToken

abstract class AbstractLexerTokenFactory: LexerTokenFactory {
    protected abstract val regExpr: Regex
    protected abstract val tokenCollection: MutableList<LexerToken>

    protected abstract fun appendToken(text: String)

    override fun matchOne(input: CharSequence): CharSequence {
        val hasMatch = regExpr.find(input, 0)
        hasMatch?. let {
            val tokenString = it.value
            appendToken(tokenString)
            val lastIndex = it.range.last + 1
            return input.drop(lastIndex)
        }

        return input
    }

    override fun matchOneSecondary(position: Int): Int {
        return position
    }
}