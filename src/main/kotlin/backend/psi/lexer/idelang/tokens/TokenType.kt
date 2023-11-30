package backend.psi.lexer.idelang.tokens

enum class TokenType {
    SEPARATOR,
    IDENTIFIER,
    SPECIAL_SYMBOL,
    STRING,
    NUMBER,
    BOOLEAN,
    TYPE,
    KEYWORD,
    OPERATOR,
    UNDEFINED
}