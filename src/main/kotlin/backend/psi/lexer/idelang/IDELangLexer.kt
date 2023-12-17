package backend.psi.lexer.idelang

import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.factories.*
import backend.psi.lexer.idelang.tokens.LexerToken

class IDELangLexer: Lexer {
    private fun primary(tokens: MutableList<LexerToken>): List<LexerTokenFactory> {
        return listOf(
            IdentifierTokenFactory(tokens),
            SeparatorTokenFactory(tokens),
            OperatorTokenFactory(tokens),
            SpecialSTokenFactory(tokens),
            StringTokenFactory(tokens),
            NumberTokenFactory(tokens)
        )
    }

    private fun secondary(tokens: MutableList<LexerToken>): List<LexerTokenFactory> {
        return listOf(
            KeywordTokenFactory(tokens),
            BooleanTokenFactory(tokens),
            TypeTokenFactory(tokens)
        )
    }

    private fun undefined(tokens: MutableList<LexerToken>): UndefinedTokenFactory = UndefinedTokenFactory(tokens)

    override fun process(text: String): List<LexerToken> {
        val tokens = mutableListOf<LexerToken>()
        var previousValue: CharSequence
        var currentValue: CharSequence = text
        val undefinedFactory = undefined(tokens)
        while (currentValue.isNotEmpty()){
            previousValue = currentValue
            primary(tokens).forEach {
                currentValue = it.matchOne(currentValue)
            }
            if(currentValue == previousValue) {
                currentValue = undefinedFactory.matchOne(currentValue)
            }
        }
        var index = 0
        val tokenCount = tokens.size
        while (index < tokenCount) {
            for(factory in secondary(tokens)){
                if(factory.matchOneSecondary(index) > index)
                    break
            }
            index++
        }
        return tokens
    }
}