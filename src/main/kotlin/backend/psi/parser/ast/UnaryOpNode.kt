package backend.psi.parser.ast

import backend.psi.lexer.idelang.tokens.TokenType

class UnaryOpNode(val operator: TokenType, private val node: AstNode) : AstNode {
    private val _children = listOf(node)

    override val children: List<AstNode>
        get() = _children
}