package backend.psi.parser.idelang

import backend.psi.lexer.idelang.tokens.LexerToken
import backend.psi.lexer.idelang.tokens.TokenType
import backend.psi.parser.Parser
import backend.psi.parser.ast.*

class IDELangParser(private val tokens: List<LexerToken>): Parser {
    private var index = -1
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
        while (tokens[index].type == TokenType.SEPARATOR && index < tokens.size)
            index++
        if(index < tokens.size) {
            currentToken = tokens[index]
        }
        return currentToken
    }

    fun parseBinOp(func: () -> ParseResult, allowed: List<TokenType>): ParseResult {
        val res = ParseResult()
        var leftNode = res.register(func())
        res.exception?.let { return res }

        while(allowed.contains(currentToken.type)) {
            val binOp = currentToken
            res.register(advance())
            val right = res.register(func())
            res.exception?.let { return res }
            leftNode = BinOpNode(binOp.type, listOf(leftNode as AstNode, right as AstNode))
        }
        return res.success(leftNode as AstNode)
    }

    private fun factorInt(): ParseResult {
        val res = ParseResult()
        val token = currentToken
        if(token.type == TokenType.PLUS || token.type == TokenType.MINUS) {
            res.register(advance())
            val factor = res.register(factorInt())
            res.exception?.let { return res }
            return res.success(UnaryOpNode(token.type, factor as AstNode))
        } else if (token.type == TokenType.NUMBER) {
            res.register(advance())
            return res.success(NumberNode(token.data))
        } else if (token.type == TokenType.LBRACE) {
            res.register(advance())
            val expr = res.register(parseIntExpr())
            res.exception?.let { return res }
            if(currentToken.type == TokenType.RBRACE) {
                res.register(advance())
                return res.success(expr as AstNode)
            } else {
                return res.failure(IllegalStateException("')' token expected"))
            }
        }

        return res.failure(IllegalStateException("Expected int or float"))
    }

    private fun factorBool(): ParseResult {
        val res = ParseResult()
        val token = currentToken
        if(token.type == TokenType.NOT) {
            res.register(advance())
            val factor = res.register(factorBool())
            res.exception?.let {
                return res
            }
            return res.success(UnaryOpNode(token.type, factor as AstNode))
        } else if (token.type == TokenType.BOOLEAN) {
            res.register(advance())
            return res.success(NumberNode(token.data))
        } else if (token.type == TokenType.LBRACE) {
            res.register(advance())
            val expr = res.register(parseBoolExpr())
            res.exception?.let { return res }
            if(currentToken.type == TokenType.RBRACE) {
                res.register(advance())
                return res.success(expr as AstNode)
            } else {
                return res.failure(IllegalStateException("')' token expected"))
            }
        }

        return res.failure(IllegalStateException("Expected boolean"))
    }

    fun parseString(): ParseResult {
        val res = ParseResult()
        if(currentToken.type == TokenType.STRING) {
            val value = currentToken.data
            res.register(advance())
            return res.success(StringNode(value))
        }
        return res.failure(IllegalStateException("String expected"))
    }

    private fun parseIntTerm(): ParseResult = parseBinOp(::factorInt, listOf(TokenType.MULT, TokenType.DIV))
    private fun parseStringTerm(): ParseResult = parseBinOp(::parseString, listOf(TokenType.MULT, TokenType.DIV))
    private fun parseBoolTerm(): ParseResult = parseBinOp(::factorBool, listOf(TokenType.AND))
    private fun parseIntExpr(): ParseResult = parseBinOp(::parseIntTerm, listOf(TokenType.PLUS, TokenType.MINUS))
    private fun parseStringExpr(): ParseResult = parseBinOp(::parseStringTerm, listOf(TokenType.CONCAT))
    private fun parseBoolExpr(): ParseResult = parseBinOp(::parseBoolTerm, listOf(TokenType.LT, TokenType.MT, TokenType.EQUAL))

    fun parseId(): ParseResult {
        val res = ParseResult()
        if(currentToken.type == TokenType.IDENTIFIER) {
            val id = currentToken
            res.register(advance())
            return res.success(IdNode(id.data))
        }
        return res.failure(IllegalStateException("Identifier expected."))
    }

    fun parseInitialization(): ParseResult {
        val possibilities = listOf(
            ::parseIntExpr,
            ::parseStringExpr,
            ::parseBoolExpr,
            //::parseFunctionCall
        )

        val res = ParseResult()
        if(currentToken.type == TokenType.VAR) {
            res.register(advance())
            val id = res.register(parseId())
            res.exception?.let { return res }
            if(currentToken.type == TokenType.ASSIGN) {
                res.register(advance())
                val rememberIndex = index
                repeat(3) {
                    val result = possibilities[it].invoke()
                    result.result?.let { it1 ->
                        res.register(result)
                        return res.success(BinOpNode(TokenType.ASSIGN, listOf(id as IdNode, it1)))
                    }
                    // try again
                    index = rememberIndex
                }
                return res.failure(IllegalStateException("You can assign either to Int, String or Bool."))
            }
            return res.failure(IllegalStateException("Assignment should contain '=' after Identifier."))
        }
        return res.failure(IllegalStateException("Initialization should start from 'var'."))
    }

    private fun parseAsSemicolonEndExpression(func: () -> ParseResult): ParseResult {
        val res = ParseResult()
        val value = res.register(func())
        res.exception?.let { return res }
        if(currentToken.type == TokenType.SEMICOLON) {
            res.register(advance())
            res.exception?.let { return res }
            return res.success(value as AstNode)
        }
        return res.failure(IllegalStateException("';' expected in the end of expression"))
    }

    private fun parseEOF(): ParseResult {
        val res = ParseResult()
        if(index == tokens.size) {
            return res.success(EOFNode())
        }
        return res.failure(IllegalStateException("Not the whole file was parsed"))
    }
    private fun program(): ParseResult {
        val res = ParseResult()
        val value = res.register(parseInitialization())
        res.exception?.let { return res }
        val eof = res.register(parseEOF())
        res.exception?.let { return res }
        return res.success(ProgramNode(listOf(value as AstNode, eof as AstNode)))
    }

    override fun parse(): ParseResult =
        try {
            program()
        } catch (e: Exception) {
            ParseResult().failure(e)
        }
}