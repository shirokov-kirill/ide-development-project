package backend.psi.lexer.idelang

import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.factories.IdentifierTokenFactory
import backend.psi.lexer.idelang.factories.SeparatorTokenFactory
import backend.psi.lexer.idelang.factories.SpecialSTokenFactory
import backend.psi.lexer.idelang.factories.UndefinedTokenFactory
import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.SpecialSToken
import backend.psi.lexer.idelang.tokens.UndefinedToken

class IDELangLexer: Lexer {
    override val tokens: MutableList<LexerToken> = mutableListOf()

    private val factories = listOf(
        IdentifierTokenFactory(tokens),
        SeparatorTokenFactory(tokens),
        SpecialSTokenFactory(tokens)
    )

    private val undefinedTokenFactory = UndefinedTokenFactory(tokens)

    override fun process(text: String) {
        var previousValue: CharSequence = ""
        var currentValue: CharSequence = text
        while (currentValue.isNotEmpty()){
            previousValue = currentValue
            factories.forEach {
                currentValue = it.matchOne(currentValue)
            }
            if(currentValue == previousValue) {
                currentValue = undefinedTokenFactory.matchOne(currentValue)
            }
        }
    }
}