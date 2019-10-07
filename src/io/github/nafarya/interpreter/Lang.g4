grammar Lang;

@header {
import java.util.HashMap;
import java.util.Map;
}

@members {
HashMap mp = new HashMap();
}

prog
    : function*
    ;

function
    : 'func' NAME '(' funcDeclArgs ')' '{'  funcBody '}'
    ;

funcDeclArgs
    : NAME*
    ;

funcBody
    : statement*
    ;

ifBranch
    : statement*
    ;

forbody
    : statement*
    ;

statement
    : ret
    | print
    | assignment
    | ifclause
    | forloop
    | funccall
    ;

funccall
    : NAME '(' funcargs ')'
    ;

funcargs
    : atom*
    ;

forloop
    : 'for' forPreaction? SEMICOLON forPredicate? SEMICOLON forPostaction? '{' forbody '}'
    ;

forPreaction
    : assignmentBody
    ;

forPredicate
    : atom
    ;

forPostaction
    : assignmentBody
    ;

condition
    : expr
    ;

ifclause
    : 'if' '(' ifPredicate ')' '{' leftBranch '}' rightBranch
    ;

ifPredicate
    : expr
    ;

leftBranch
    : ifBranch
    ;

rightBranch
    : ('else' '{' ifBranch '}')?
    ;

ret
    : 'return' expr SEMICOLON
    | 'return' NAME SEMICOLON
    ;

assignment
    : assignmentBody SEMICOLON
    ;

assignmentBody
    : NAME EQ (expr | funccall)
    ;

print
    : 'print' '(' atom ')' SEMICOLON
    ;

expr
    : mulExpr term*
    ;

term
    : ((ADD | SUB) mulExpr)
    ;

mulExpr
    : atom factor*
    ;

factor
    : ((MUL | DIV) atom);

atom
    : NUM
    | NAME
    | '(' expr ')'
    | funccall
    ;

NAME : [a-zA-z] [a-zA-Z0-9]*;
EQ : '=';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
SEMICOLON : ';';
NUM : [0-9]+ ;
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines