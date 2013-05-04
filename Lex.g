// COMS22303: Lexical analyser
lexer grammar Lex;

@members
{
  public void displayRecognitionError(String[] tokenNames,
                                      RecognitionException e) {
      String hdr = getErrorHeader(e);
      String msg = getErrorMessage(e, tokenNames);
      errorReporter.reportError(hdr, msg, e);
  }

  private IErrorReporter errorReporter = null;

  public void setErrorReporter(IErrorReporter errorReporter) {
      this.errorReporter = errorReporter;
  }

}

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

IDENTIFIER  : CHARACTER (ALPHANUM)*;


//---------------------------------------------------------------------------
// OPERATORS
//---------------------------------------------------------------------------
SEMICOLON    : ';' ;
OPENPAREN    : '(' ;
CLOSEPAREN   : ')' ;
MORETHAN     : '>' ;
LESSTHAN     : '<' ;
MOREOREQ     : '>=' ;
LESSOREQ     : '<=' ;
NOTEQUAL     : '!=' ;
EQUAL        : '=' ;
MUL          : '*' ;
DIV          : '/' ;
PLUS         : '+' ;
MINUS        : '-' ;
ASSIGN       : ':=' ;

fragment
INT          : ('0'..'9')+ ;

fragment
EXPONENT     : 'e' ('-')? INT;

REALNUM      : INT '.' INT (EXPONENT)?;

fragment
CHARACTER   : ('a'..'z'|'A'..'Z');

fragment
ALPHANUM    : (CHARACTER | INT);

STRING       : '\'' ('\'' '\'' | ~'\'')* '\'';

COMMENT      : '{' (~'}')* '}' {skip();} ;

WS           : (' ' | '\t' | '\r' | '\n' )+ {skip();};
