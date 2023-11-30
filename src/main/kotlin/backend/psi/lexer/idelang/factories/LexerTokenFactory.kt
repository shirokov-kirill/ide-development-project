package backend.psi.lexer.idelang.factories

interface LexerTokenFactory {
    fun matchOne(input: CharSequence): CharSequence

    /*
     * Return value is bigger than parameter if match found
     * Otherwise return value equals the parameter
     */
    fun matchOneSecondary(position: Int): Int
}