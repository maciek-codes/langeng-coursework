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

variable :
  v=IDENTIFIER {$v.text.length() < 10}?
  ;
catch [FailedPredicateException fpe] {
  String hdr = getErrorHeader(fpe);
  String msg = "identifier "+$v.text+" is longer than 9 characters.";
  errorReporter.reportError(hdr, msg, fpe);
}


program :
  compoundstatement {!errorReporter.hasErrors()}? { errorReporter.displayErrors();}
  ;
catch [FailedPredicateException fpe] {
  errorReporter.displayErrors();
  System.exit(0);
}


compoundstatement :
    BEGIN^ ( statement SEMICOLON! )* END!
  ;

relation :
  (MORETHAN | LESSTHAN | MOREOREQ | LESSOREQ | NOTEQUAL | EQUAL)
  ;

statement :
    WRITE^ OPENPAREN! ( expression | string ) CLOSEPAREN!
  | WRITELN^
  | READ^ OPENPAREN! (variable) CLOSEPAREN! { errorReporter.addVariable($variable.text); }
  | IF^ exp compoundstatement (ELSE! compoundstatement)?
  | REPEAT^ compoundstatement UNTIL! exp
  | variable ASSIGN^ expression { errorReporter.addVariable($variable.text); }
  ;

exp:
  expression relation^ expression
  ;

factor :
  variable 
  {
    if(!errorReporter.hasVariable($variable.text)) {
      String msg = "variable "+$variable.text+" is not initialized before first use.";
      errorReporter.reportWarning(msg, $variable.start.getLine());
    } 
  }
  | constant
  | OPENPAREN! (expression) CLOSEPAREN!
  ;

term :
  factor ( (MUL^ | DIV^) factor)*
  ;

expression :
    (PLUS^ | MINUS^ )? term ((PLUS^ | MINUS^) term)*
  ;

constant :
    REALNUM
  ;

string
    scope { String tmp;}
    :
    s=STRING { $string::tmp = cleanString($s.text); }-> STRING[$string::tmp]
  ;


