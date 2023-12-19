package backend.psi.parser.idelang

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.TokenType
import backend.psi.parser.Parser
import backend.psi.parser.ast.*
import kotlin.math.max
import kotlin.math.min

class IDELangParser(private val tokens: List<LexerToken>): Parser {
    private var index = -1
        set(value) {
            val index = max(min(tokens.lastIndex, value), 0)
            currentToken = tokens[index]
            field = value
        }

    private lateinit var currentToken: LexerToken

    class ParseResult(var exception: Exception? = null, var result: AstNode? = null) {
        fun register(result: Any): Any? {
            if(result is ParseResult){
                result.exception?.let {
                    exception = it
                }
                return result.result
            }
            return result
        }

        fun success(node: AstNode): ParseResult {
            result = node
            return this
        }

        fun failure(error: Exception): ParseResult {
            exception = error
            return this
        }
    }

    init {
        advance()
    }

    private fun advance(): LexerToken {
        index++
        while (index < tokens.size && tokens[index].type == TokenType.SEPARATOR)
            index++
        return currentToken
    }

    private fun parseBinOp(func: () -> ParseResult, allowed: List<TokenType>): ParseResult {
        val possibilities = listOf(
            func,
            ::parseId
        )
        val res = ParseResult()
        var leftNode: AstNode? = null
        val leftResults = parseOneOf(possibilities)
        leftResults.forEach forEach1@{ pair ->
            pair.first.result?.let { it1 ->
                if(allowed.contains(tokens[pair.second].type)) {
                    index = pair.second
                    val binOp = currentToken
                    res.register(advance())
                    val rightResults = parseOneOf(possibilities)
                    rightResults.forEach forEach2@{ pair2 ->
                        pair2.first.result?.let { it2 ->
                            index = pair2.second
                            leftNode = BinOpNode(binOp.type, listOf(it1, it2))
                            return@forEach2
                        }
                    }
                } else {
                    index = pair.second
                    leftNode = it1
                }
            }
        }
        leftNode?.let {
            return res.success(leftNode as AstNode)
        }
        return res.failure(IllegalStateException("Left part should either be $func or Id."))
    }

    private fun parseExpressionInBraces(func: () -> ParseResult): ParseResult {
        val res = ParseResult()
        if(currentToken.type == TokenType.LBRACE) {
            res.register(advance())
            val expr = res.register(func())
            res.exception?.let { return res }
            if(currentToken.type == TokenType.RBRACE) {
                res.register(advance())
                return res.success(expr as AstNode)
            }
            return res.failure(IllegalStateException("No closing brace found."))
        }
        return res.failure(IllegalStateException("No braces found."))
    }

    private fun factorInt(): ParseResult {
        val res = ParseResult()
        val token = currentToken
        when (token.type) {
            TokenType.MINUS -> {
                res.register(advance())
                val factor = res.register(factorInt())
                res.exception?.let { return res }
                return res.success(UnaryOpNode(token.type, factor as AstNode))
            }

            TokenType.NUMBER -> {
                res.register(advance())
                return res.success(NumberNode(token.data))
            }

            TokenType.LBRACE -> {
                val expr = res.register(parseExpressionInBraces(::parseIntExpr))
                res.exception?.let { return res }
                return res.success(expr as AstNode)
            }
            else -> return res.failure(IllegalStateException("Expected int or float"))
        }
    }

    private fun factorBool(): ParseResult {
        val res = ParseResult()
        val token = currentToken
        when (token.type) {
            TokenType.NOT -> {
                res.register(advance())
                val factor = res.register(factorBool())
                res.exception?.let {
                    return res
                }
                return res.success(UnaryOpNode(token.type, factor as AstNode))
            }

            TokenType.BOOLEAN -> {
                res.register(advance())
                return res.success(BooleanNode(token.data))
            }

            TokenType.LBRACE -> {
                res.register(advance())
                val possibilities = listOf(
                    ::parseBoolExpr,
                    ::parseIntToBooleanExpression,
                    ::parseStringToBooleanExpression,
                )
                val results = parseOneOf(possibilities)
                results.forEach { pair ->
                    pair.first.result?.let {
                        if(tokens[pair.second].type == TokenType.RBRACE) {
                            index = pair.second
                            res.register(advance())
                            return res.success(it)
                        }
                    }
                }
                return res.failure(IllegalStateException("Expression in brackets should be convertable to boolean."))
            }
            else -> return res.failure(IllegalStateException("Expected boolean"))
        }
    }

    private fun parseString(): ParseResult {
        val res = ParseResult()
        val token = currentToken
        if(token.type == TokenType.STRING) {
            res.register(advance())
            return res.success(StringNode(token.data))
        } else if (currentToken.type == TokenType.IDENTIFIER) {
            res.register(advance())
            return res.success(IdNode(token.data))
        }
        return res.failure(IllegalStateException("String expected"))
    }


    // Expressions
    private fun parseIntExpr(): ParseResult = parseBinOp(::parseIntTerm, listOf(TokenType.PLUS, TokenType.MINUS))
    private fun parseStringExpr(): ParseResult = parseBinOp(::parseString, listOf(TokenType.CONCAT))
    private fun parseBoolExpr(): ParseResult = parseBinOp(::parseBoolTerm, listOf(TokenType.EQUAL))

    // Terms
    private fun parseIntTerm(): ParseResult = parseBinOp(::factorInt, listOf(TokenType.MULT, TokenType.DIV))
    private fun parseBoolTerm(): ParseResult = parseBinOp(::factorBool, listOf(TokenType.AND))

    // Transition to Bool expressions
    private fun parseIntToBooleanExpression() = parseBinOp(::parseIntExpr, listOf(TokenType.EQUAL, TokenType.MT, TokenType.LT))
    private fun parseStringToBooleanExpression() = parseBinOp(::parseStringExpr, listOf(TokenType.EQUAL))

    private fun parseId(): ParseResult {
        val res = ParseResult()
        val token = currentToken
        if(token.type == TokenType.IDENTIFIER) {
            res.register(advance())
            return res.success(IdNode(token.data))
        }
        return res.failure(IllegalStateException("Identifier expected."))
    }

    private fun parseAssignment(): ParseResult {
        val possibilities = listOf(
            ::parseIntExpr,
            ::parseStringExpr,
            ::parseBoolExpr
        )
        val res = ParseResult()
        val id = res.register(parseId())
        res.exception?.let { return res }
        if(currentToken.type == TokenType.ASSIGN) {
            res.register(advance())
            val results = parseOneOf(possibilities)
            results.forEach { pair ->
                pair.first.result?.let {
                    if(tokens[pair.second].type == TokenType.SEMICOLON) {
                        index = pair.second
                        return res.success(AssignmentNode(listOf(id as AstNode, it)))
                    }
                }
            }
            return res.failure(IllegalStateException("You can assign either to Int, String or Bool."))
        }
        return res.failure(IllegalStateException("Assignment should contain '=' after Identifier."))
    }

    private fun parseInitialization1(): ParseResult {
        val res = ParseResult()
        if(currentToken.type == TokenType.VAR) {
            res.register(advance())
            val assignment = res.register(parseAssignment())
            res.exception?.let { return res }
            return res.success(assignment as AstNode)
        }
        return res.failure(IllegalStateException("Initialization should start from 'var'."))
    }

    private fun parseInitialization(): ParseResult {
        val res = ParseResult()
        val value = res.register(parseInitialization1())
        res.exception?.let { return res }
        if(currentToken.type == TokenType.SEMICOLON) {
            res.register(advance())
            res.exception?.let { return res }
            return res.success(value as AstNode)
        }
        return res.failure(IllegalStateException("';' expected in the end of expression"))
    }

    private fun parseFunctionCall(): ParseResult {
        val res = ParseResult()
        val id = res.register(parseId())
        res.exception?.let { return res }
        val arguments = res.register(parseCall(false))
        res.exception?.let { return res }
        return res.success(CallNode(listOf(id as AstNode, arguments as AstNode)))
    }

    private fun parseIf(): ParseResult {
        TODO()
    }

    private fun parseWhile(): ParseResult {
        TODO()
    }

    private fun parseFunc(): ParseResult = parseCallable(TokenType.FUNC, ::parseFunctionBody)

    private fun parseProc(): ParseResult = parseCallable(TokenType.PROC, ::parseProcedureBody)

    private fun parseEOF(): ParseResult {
        val res = ParseResult()
        if(index == tokens.size) {
            return res.success(EOFNode())
        }
        return res.failure(IllegalStateException("Not the whole file was parsed"))
    }

    private fun parseOneOf(possibilities: List<() -> ParseResult>): List<Pair<ParseResult, Int>> {
        val resultList = mutableListOf<Pair<ParseResult,Int>>()
        val rememberIndex = index
        repeat(possibilities.size) {
            val result = possibilities[it].invoke()
            resultList.add(Pair(result, index))
            index = rememberIndex
        }
        return resultList
    }

    private fun parseReturnStatement(exprAllowed: Boolean): ParseResult {
        val possibilities = listOf(
            ::parseIntExpr,
            ::parseStringExpr,
            ::parseBoolExpr
        )
        val res = ParseResult()
        val token = currentToken
        if(token.type == TokenType.RETURN) {
            res.register(advance())
            if(exprAllowed) {
                val rememberIndex = index
                val results = parseOneOf(possibilities)
                results.forEach {
                    index = it.second
                    it.first.result?.let { it1 ->
                        if(currentToken.type == TokenType.SEMICOLON) {
                            res.register(advance())
                            return res.success(ReturnNode(listOf(it1)))
                        }
                        return res.failure(IllegalStateException("Semicolon expected in the end of return statement."))
                    }
                    index = rememberIndex
                }
            }
            if(currentToken.type == TokenType.SEMICOLON) {
                res.register(advance())
                return res.success(ReturnNode(listOf()))
            }
            return res.failure(IllegalStateException("Semicolon expected in the end of return statement."))
        }
        return res.failure(IllegalStateException("Return statement should start with 'return' keyword."))
    }

    private fun parseStatement(): ParseResult {
        val possibilities1 = listOf(
            ::parseAssignment,
            ::parseInitialization,
            ::parseFunctionCall
        )
        val possibilities2 = listOf(
            ::parseProc,
            ::parseFunc,
            ::parseWhile,
            ::parseIf
        )
        val res = ParseResult()
        val results1 = parseOneOf(possibilities1)
        results1.forEach { pair ->
            pair.first.result?.let {
                if(tokens[pair.second].type == TokenType.SEMICOLON) {
                    index = pair.second
                    res.register(advance())
                    return res.success(it)
                }
                return res.failure(IllegalStateException("Semicolon expected"))
            }
        }

        val results2 = parseOneOf(possibilities1)
        results2.forEach { pair ->
            pair.first.result?.let {
                index = pair.second
                return res.success(it)
            }
        }
        return res.failure(IllegalStateException("Statement expected"))
    }

    private fun parseCallableBody(allowReturnExpr: Boolean): ParseResult {
        val res = ParseResult()
        val children = mutableListOf<AstNode>()
        while (currentToken.type != TokenType.RETURN) {
            val result = parseStatement()
            result.exception?.let { return res.failure(it) }
            result.result?.let { children.add(it) }
        }
        val returnStatement = parseReturnStatement(allowReturnExpr)
        returnStatement.exception?.let { res.failure(it) }
        returnStatement.result?.let { children.add(it) }
        return res.success(ProgramNode(children))
    }

    private fun parseFunctionBody() = parseCallableBody(true)
    private fun parseProcedureBody() = parseCallableBody(false)

    private fun parseType(): ParseResult {
        val res = ParseResult()
        val token = currentToken
        if(token.type == TokenType.TYPE) {
            res.register(advance())
            return when(token.data) {
                "string" -> res.success(TypeNode(TokenType.STRING))
                "number" -> res.success(TypeNode(TokenType.NUMBER))
                "boolean" -> res.success(TypeNode(TokenType.BOOLEAN))
                else -> res.failure(IllegalStateException("Unknown type"))
            }
        }
        return res.failure(IllegalStateException("Type expected"))
    }

    private fun parseParameter(asParameter: Boolean): ParseResult {
        val res = ParseResult()
        val id = res.register(parseId())
        res.exception?.let { return res }
        if(asParameter && currentToken.type == TokenType.DOUBLEDOT) {
            res.register(advance())
            val type = res.register(parseType())
            res.exception?.let { return res }
            return res.success(ParameterNode(listOf(id as AstNode, type as AstNode)))
        }
        return res.success(ParameterNode(listOf(id as AstNode)))
    }

    private fun parseCall(asParameters: Boolean): ParseResult {
        val res = ParseResult()
        val children = mutableListOf<AstNode>()
        if(currentToken.type == TokenType.LBRACE) {
            res.register(advance())
            while (true) {
                val child = res.register(parseParameter(asParameters))
                res.exception?.let { return res }
                children.add(child as AstNode)
                if(currentToken.type == TokenType.COMMA) {
                    res.register(advance())
                } else {
                    break
                }
            }
            if(currentToken.type == TokenType.RBRACE) {
                res.register(advance())
                return res.success(CollectionNode(children))
            }
            return res.failure(IllegalStateException("Right brace is not found."))
        }
        return res.failure(IllegalStateException("Open brace '(' symbol expected.")) // (
    }

    private fun parseCallable(prefix: TokenType, func: () -> ParseResult): ParseResult {
        val res = ParseResult()
        if(currentToken.type == prefix) {
            res.register(advance())
            val id = res.register(parseId())
            res.exception?.let { return res }
            val parameters = res.register(parseCall(true))
            res.exception?.let { return res }
            if(currentToken.type == TokenType.CURLY_LBRACE) {
                res.register(advance())
                val body = res.register(func())
                res.exception?.let { return res }
                if(currentToken.type == TokenType.CURLY_RBRACE) {
                    res.register(advance())
                    return res.success(FunctionNode(listOf(id as AstNode, parameters as AstNode, body as AstNode)))
                }
                return res.failure(IllegalStateException("No closing brace found: '}'."))
            }
            return res.failure(IllegalStateException("Function initialization missed.")) // {
        }
        return res.failure(IllegalStateException("Function should start with 'func' keyword.")) // func
    }

    private fun program(): ParseResult {
        val possibilities = mutableListOf(
            ::parseProc,
            ::parseFunc,
            ::parseInitialization,
        )
        val res = ParseResult()
        val children = mutableListOf<AstNode>()
        while (index != tokens.size) {
            val results = parseOneOf(possibilities)
            results.forEach { pair ->
                pair.first.result?.let {
                    index = pair.second
                    children.add(it)
                }
            }
        }
        val eof = res.register(parseEOF())
        res.exception?.let { return res }
        children.add(eof as AstNode)
        return res.success(ProgramNode(children))
    }

    override fun parse(): ParseResult =
        try {
            program()
        } catch (e: Exception) {
            ParseResult().failure(e)
        }
}