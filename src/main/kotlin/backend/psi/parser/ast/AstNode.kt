package backend.psi.parser.ast

interface AstNode {
    val children: List<AstNode>
}