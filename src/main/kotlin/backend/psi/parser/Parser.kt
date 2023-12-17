package backend.psi.parser

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.parser.ast.AstNode
import backend.psi.parser.idelang.IDELangParser

interface Parser {
    fun parse(): IDELangParser.ParseResult
}