// COMS22303: Syntax analyser

parser grammar Syn;

options {
  tokenVocab = Lex;
  output = AST;
}

@members
{
	private String cleanString(String s){
		String tmp;
		tmp = s.replaceAll("^'", "");
		s = tmp.replaceAll("'$", "");
		tmp = s.replaceAll("''", "'");
		return tmp;
	}
}

program :
    compoundstatement
  ;

compoundstatement :
    BEGIN^ ( statement SEMICOLON! )* END!
  ;

statement :
    WRITE^ OPENPAREN! ( expression | string ) CLOSEPAREN!
  | WRITELN
  | READ^ OPENPAREN! (variable) CLOSEPAREN!
  | IF OPENPARENT! ( expression relation expression )
    compoundstatement
  ;

expression:
    UNARYOP? term ( ( PLUS | MINUS ) term)*
  ;

term:
  factor ( (MUL | DIV) factor)?

factor:
  variable
  | constant
  | OPENPAREN^ (expression) CLOSEPAREN!

constant:
    REALNUM 
  ;

string
    scope { String tmp;}
    :
    s=STRING { $string::tmp = cleanString($s.text); }-> STRING[$string::tmp]
  ;


variable
  IDENTIFIER
  ;
