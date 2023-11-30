// DELETE THIS CONTENT IF YOU PUT COMBINED GRAMMAR IN Parser TAB
lexer grammar IDELangLexer;

AND : '&&' ;
OR : '||' ;
NOT : '!' ;

EQ : '==' ;
IS : '=' ;

PLUS : [ ]* '+' [ ]* ;
MINUS : '-' ;
MULT : '*' ;
DIV : '/' ;
INTMORE : '>' ;
INTLESS : '<' ;

CONCAT : '%' ;

COMMA : ',' ;
SEMI : ';' ;
LPAREN : '(' ;
RPAREN : ')' ;
LCURLY : '{' ;
RCURLY : '}' ;
DDOTS : ':' ;

IF: 'if' ;
ELSE: 'else' ;

WHILE: 'while' ;

FUNC : 'func' ;
RETURN : 'return' ;
PROCEDURE : 'proc' ;

PRINT: 'print' ;                                !!!

VAR : 'var' ;
INT : [0-9]+ ;
STR : '"' .*? '"' ;
BOOL : 'true'
     | 'false'
     ;
     
TYPE : 'number'
     | 'string'
     | 'boolean'
     ;
     
ID : [a-zA-Z_][a-zA-Z_0-9]* ;
WS : [ \t\n\r\f]+ -> skip ;