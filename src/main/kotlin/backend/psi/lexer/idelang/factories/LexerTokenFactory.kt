package backend.psi.lexer.idelang.factories

interface LexerTokenFactory {
    fun matchOne(input: CharSequence): CharSequence
}