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

variable :
  IDENTIFIER
  ;

program :
    compoundstatement
  ;

compoundstatement :
    BEGIN^ ( statement SEMICOLON! )* END!
  ;

relation :
  (MORETHAN | LESSTHAN | MOREOREQ | LESSOREQ | NOTEQUAL | EQUAL)
  ;

statement :
    WRITE^ OPENPAREN! ( expression | string ) CLOSEPAREN!
  | WRITELN
  | READ^ OPENPAREN! variable CLOSEPAREN!
  | IF OPENPARENT! expression relation expression CLOSEPAREN!
  | compoundstatement
  | variable ASSIGN! expression
  ;

factor :
  variable
  | constant
  | OPENPAREN^ (expression) CLOSEPAREN!
  ;

term :
  factor ( (MUL | DIV) factor)*
  ;

expression :
    UNARYOP term ((PLUS|MINUS) term)*
  ;

constant :
    REALNUM 
  ;

string
    scope { String tmp;}
    :
    s=STRING { $string::tmp = cleanString($s.text); }-> STRING[$string::tmp]
  ;


