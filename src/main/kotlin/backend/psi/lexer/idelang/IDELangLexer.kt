package backend.psi.lexer.idelang

import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.factories.*
import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.SpecialSToken
import backend.psi.lexer.idelang.tokens.UndefinedToken

class IDELangLexer: Lexer {
    override val tokens: MutableList<LexerToken> = mutableListOf()

    private val primaryFactories = listOf(
        IdentifierTokenFactory(tokens),
        SeparatorTokenFactory(tokens),
        OperatorTokenFactory(tokens),
        SpecialSTokenFactory(tokens),
        StringTokenFactory(tokens),
        NumberTokenFactory(tokens)
    )

    private val secondaryFactories = listOf(
        KeywordTokenFactory(tokens),
        BooleanTokenFactory(tokens),
        TypeTokenFactory(tokens)
    )

    private val undefinedTokenFactory = UndefinedTokenFactory(tokens)

    override fun process(text: String) {
        var previousValue: CharSequence
        var currentValue: CharSequence = text
        while (currentValue.isNotEmpty()){
            previousValue = currentValue
            primaryFactories.forEach {
                currentValue = it.matchOne(currentValue)
            }
            if(currentValue == previousValue) {
                currentValue = undefinedTokenFactory.matchOne(currentValue)
            }
        }
        var index = 0
        val tokenCount = tokens.size
        while (index < tokenCount) {
            for(factory in secondaryFactories){
                if(factory.matchOneSecondary(index) > index)
                    break
            }
            index++
        }
    }
}