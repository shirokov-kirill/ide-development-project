package backend.psi.parser.ast

import backend.psi.parser.ast.scopes.LanguageScope

/*
 * Implementation of this interface contains a scope
 */
interface ScopeContainer {
    val scope: LanguageScope
}