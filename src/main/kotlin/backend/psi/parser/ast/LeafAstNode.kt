package backend.psi.parser.ast

open class LeafAstNode: AstNode {
    override val children: List<AstNode>
        get() = listOf()
}