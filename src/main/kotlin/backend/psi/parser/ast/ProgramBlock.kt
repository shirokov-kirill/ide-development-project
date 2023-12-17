package backend.psi.parser.ast

import backend.psi.parser.ast.scopes.LanguageScope

class ProgramBlock(override val children: List<AstNode>, override val scope: LanguageScope) : AstNode, ScopeContainer {
}