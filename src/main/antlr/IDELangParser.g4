parser grammar IDELangParser;
options { tokenVocab=IDELangLexer; }

program: programblock EOF
       ;

stat: ID IS expr SEMI
    | expr SEMI
    ;

decl: VAR ID IS expr SEMI
    ;

expr: ID
    | intexpr
    | strexpr
    | boolexpr
    | INT
    | STR
    | BOOL
    ;

intexpr: ID
       | INT
       | intexpr PLUS intexpr
       | intexpr MINUS intexpr
       | intexpr MULT intexpr
       | intexpr DIV intexpr
       | LPAREN intexpr RPAREN
       ;
       
strexpr: ID
       | STR
       | strexpr CONCAT strexpr
       | LPAREN strexpr RPAREN
       ;

boolexpr: ID
       | BOOL
       | boolexpr AND boolexpr
       | boolexpr OR boolexpr
       | boolexpr EQ boolexpr
       | NOT boolexpr
       | LPAREN boolexpr RPAREN
       | intexpr INTMORE intexpr
       | intexpr INTLESS intexpr
       ;
       
ifblock: IF LPAREN boolexpr RPAREN LCURLY programblock RCURLY
       | IF LPAREN boolexpr RPAREN LCURLY programblock RCURLY elseblock
       ;

elseblock: ELSE LCURLY programblock RCURLY
         ;

programblock: stat
            | decl
            | ifblock
            | whileloop
            | function
            | procedure
            | print
            | returnblock
            | stat programblock
            | decl programblock
            | ifblock programblock
            | whileloop programblock
            | function programblock
            | procedure programblock
            | print programblock
            ;
            
whileloop: WHILE LPAREN boolexpr RPAREN LCURLY programblock RCURLY
         ;
         
function: FUNC ID LPAREN paramlist RPAREN LCURLY programblock RCURLY
        | FUNC ID LPAREN RPAREN LCURLY programblock RCURLY
        ;

paramlist: ID DDOTS TYPE
         | ID DDOTS TYPE COMMA paramlist
         ;

returnblock: RETURN expr SEMI
           ;
           
procprogramblock: stat
                | decl
                | ifblock
                | whileloop
                | function
                | procedure
                | print
                | stat procprogramblock
                | decl procprogramblock
                | ifblock procprogramblock
                | whileloop procprogramblock
                | function procprogramblock
                | procedure procprogramblock
                | print procprogramblock
                ;

procedure: PROCEDURE ID LPAREN paramlist RPAREN LCURLY procprogramblock RCURLY
         | PROCEDURE ID LPAREN RPAREN LCURLY procprogramblock RCURLY
         ;

print: PRINT LPAREN strexpr RPAREN SEMI
     ;