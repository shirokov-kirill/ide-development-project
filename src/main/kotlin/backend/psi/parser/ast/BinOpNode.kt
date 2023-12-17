package backend.psi.parser.ast

import backend.psi.lexer.idelang.tokens.TokenType

class BinOpNode(val operation: TokenType, override val children: List<AstNode>) : AstNode {
}