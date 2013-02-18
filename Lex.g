// COMS22303: Lexical analyser

lexer grammar Lex;

//---------------------------------------------------------------------------
// KEYWORDS
//---------------------------------------------------------------------------
BEGIN      : 'begin' ;
END        : 'end' ;
WRITE      : 'write' ;
WRITELN    : 'writeln' ;
IF         : 'if' ;
ELSE       : 'else' ;
READ       : 'read' ;
REPEAT     : 'repeat' ;
UNTIL      : 'until' ;

//---------------------------------------------------------------------------
// OPERATORS
//---------------------------------------------------------------------------
MORETHAN     : '>' ;
LESSTHAN     : '<' ;
MOREOREQ     : '>=' ;
LESSOREQ     : '<=' ;
NOTEQUAL     : '!=' ;
EQUAL        : '=' ;
SEMICOLON    : ';' ;
OPENPAREN    : '(' ;
CLOSEPAREN   : ')' ;
MUL          : '*' ;
DIV          : '\' ;

IDENTIFIER  : ( 'a'..'z' | 'A'..'Z' | INT )

REALNUM      : INT '.' INT (EXPONENT)?;

UNARYOP      : ( '+' | '-')

fragment 
EXPONENT     : 'e' ('-')? INT ;

fragment 
INT          : ('0'..'9')+ ;

STRING       : '\'' ('\'' '\'' | ~'\'')* '\'';

COMMENT      : '{' (~'}')* '}' {skip();} ;

WS           : (' ' | '\t' | '\r' | '\n' )+ {skip();} ;

