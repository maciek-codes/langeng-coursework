// COMS22303: IR tree construction
//
// This program converts an Abstract Syntax Tree produced by ANTLR to an IR tree.
// The Abstract Syntax Tree has type CommonTree and can be walked using 5 simple
// methods.  If ast is a CommonTree and t is a Token:
//
//   int        ast.getChildCount();                       // Get # of subtrees
//   CommonTree (CommonTree)ast.getChild(int childNumber); // Get a subtree
//   Token      ast.getToken();                            // Get a node's token
//   int        t.getType();                               // Get token type
//   String     t.getText();                               // Get token text
//
// Every method below has two parameters: the AST (input) and IR tree (output).
// Some methods (arg()) return the type of the item processed.

import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import antlr.collections.AST;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Irt
{
// The code below is generated automatically from the ".tokens" file of the
// ANTLR syntax analysis, using the TokenConv program.
//
// CAMLE TOKENS BEGIN
  public static final String[] tokenNames = new String[] {
"NONE", "NONE", "NONE", "NONE", "BEGIN", "END", "WRITE", "WRITELN", "IF", "ELSE", "READ", "REPEAT", "UNTIL", "CHARACTER", "ALPHANUM", "IDENTIFIER", "SEMICOLON", "OPENPAREN", "CLOSEPAREN", "MORETHAN", "LESSTHAN", "MOREOREQ", "LESSOREQ", "NOTEQUAL", "EQUAL", "MUL", "DIV", "PLUS", "MINUS", "ASSIGN", "INT", "EXPONENT", "REALNUM", "STRING", "COMMENT", "WS"};
  public static final int BEGIN=4;
  public static final int END=5;
  public static final int WRITE=6;
  public static final int WRITELN=7;
  public static final int IF=8;
  public static final int ELSE=9;
  public static final int READ=10;
  public static final int REPEAT=11;
  public static final int UNTIL=12;
  public static final int CHARACTER=13;
  public static final int ALPHANUM=14;
  public static final int IDENTIFIER=15;
  public static final int SEMICOLON=16;
  public static final int OPENPAREN=17;
  public static final int CLOSEPAREN=18;
  public static final int MORETHAN=19;
  public static final int LESSTHAN=20;
  public static final int MOREOREQ=21;
  public static final int LESSOREQ=22;
  public static final int NOTEQUAL=23;
  public static final int EQUAL=24;
  public static final int MUL=25;
  public static final int DIV=26;
  public static final int PLUS=27;
  public static final int MINUS=28;
  public static final int ASSIGN=29;
  public static final int INT=30;
  public static final int EXPONENT=31;
  public static final int REALNUM=32;
  public static final int STRING=33;
  public static final int COMMENT=34;
  public static final int WS=35;
// CAMLE TOKENS END

  public static IRTree convert(CommonTree ast)
  {
    IRTree irt = new IRTree();
    program(ast, irt);
    return irt;
  }

  // Convert a program AST to IR tree
  public static void program(CommonTree ast, IRTree irt)
  {
    statements(ast, irt);
  }

  // Convert a compoundstatement AST to IR tree
  public static void statements(CommonTree ast, IRTree irt)
  {
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == BEGIN) {
      int n = ast.getChildCount();
      if (n == 0) {
        irt.setOp("NOOP");
      }
      else {
        CommonTree ast1 = (CommonTree)ast.getChild(0);
        statements1(ast, 0, n-1, irt);
      }
    }
    else {
      error(tt);
    }
  }

  public static void statements1(CommonTree ast, int first, int last, IRTree irt)
  {
    CommonTree ast1 = (CommonTree)ast.getChild(first);
    if (first == last) {
      statement(ast1, irt);
    }
    else {
      IRTree irt1 = new IRTree();
      IRTree irt2 = new IRTree();
      statement(ast1, irt1);
      statements1(ast, first+1, last, irt2);
      irt.setOp("SEQ");
      irt.addSub(irt1);
      irt.addSub(irt2);
    }
  }

  // Convert a statement AST to IR tree
  public static void statement(CommonTree ast, IRTree irt)
  {
    CommonTree ast1;
    IRTree irt1 = new IRTree();
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == WRITELN) {
      String a = String.valueOf(Memory.allocateString("\n"));
      irt.setOp("WRS");
      irt.addSub(new IRTree("MEM", new IRTree("CONST", new IRTree(a))));
    }
    else if (tt == WRITE) {
      ast1 = (CommonTree)ast.getChild(0);
      String type = arg(ast1, irt1);
      if (type.equals("real") || type.equals("identifier")) {
        irt.setOp("WRR");
        irt.addSub(irt1);
      }
      else {
        irt.setOp("WRS");
        irt.addSub(irt1);
      }
    }
    else if (tt == ASSIGN) {
      assignment(ast, irt);
    }
    else if (tt == READ) {
      read(ast, irt);
    }
    else if(tt == MUL || tt == DIV || tt == PLUS || tt == MINUS) {
      expression(ast, irt);
    }
    else {
      error(tt);
    }
  }

  // Convert an arg AST to IR tree
  public static String arg(CommonTree ast, IRTree irt)
  {
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == STRING) {
      String tx = t.getText();
      int a = Memory.allocateString(tx);
      String st = String.valueOf(a);
      irt.setOp("MEM");
      irt.addSub(new IRTree("CONST", new IRTree(st)));
      return "string";
    }
    else if(tt == IDENTIFIER) {
      String identifier = t.getText();
      int a = Memory.getVariable(identifier);
      String st = String.valueOf(a);
      irt.setOp("MEM");
      irt.addSub(new IRTree("CONST", new IRTree(st)));
      return "identifier";
    }
    else {
      expression(ast, irt);
      return "real";
    }
  }

  // Convert an expression AST to IR tree
  public static void expression(CommonTree ast, IRTree irt)
  {
    CommonTree ast1;
    IRTree irt1 = new IRTree();
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == REALNUM) {
      constant(ast, irt1);
    }
    else if((tt == PLUS || tt == MINUS || tt == DIV || tt == MUL) &&
      ast.getChildCount() > 1) {

      arithmetic(ast, irt);
      return;
    } else if(tt == PLUS && ast.getChildCount() == 1) {
      ast1 = (CommonTree)ast.getChild(0);
      constant(ast1, irt1);
    } else if(tt == MINUS && ast.getChildCount() == 1) {
      ast1 = (CommonTree)ast.getChild(0);
      constant(ast1, irt1);
      irt1.setOp("-" + irt1.getOp());
    }
    irt.setOp("CONST");
    irt.addSub(irt1);
  }

  // Convert a constant AST to IR tree
  public static void constant(CommonTree ast, IRTree irt)
  {
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == REALNUM) {
      String tx = t.getText();
      irt.setOp(tx);
    }
    else {
      error(tt);
    }
  }

  // Convert an assignment AST to IR tree
  public static void assignment(CommonTree ast, IRTree irt)
  {
    irt.setOp("MOVE");
    CommonTree astLeft = (CommonTree)ast.getChild(0);
    CommonTree astRight = (CommonTree)ast.getChild(1);
    IRTree irtLeft = new IRTree();
    IRTree irtRight = new IRTree();
    String typeLeft = arg(astLeft, irtLeft);
    String typeRight = arg(astRight, irtRight);
    irt.addSub(irtLeft);
    irt.addSub(irtRight);
  }

  // Convert a read statement AST to IR tree
  public static void read(CommonTree ast, IRTree irt)
  {
    irt.setOp("READ");
    CommonTree argument = (CommonTree)ast.getChild(0);
    IRTree irtSub = new IRTree();
    String type = arg(argument, irtSub);
    irt.addSub(irtSub);
  }

  public static void arithmetic(CommonTree ast, IRTree irt)
  {
    irt.setOp("BINARYOP");
    IRTree irt0 = new IRTree();
    IRTree irt1 = new IRTree();
    IRTree irt2 = new IRTree();

    if(ast.getType() == PLUS) {
      irt0.setOp("PLUS");
    } else if(ast.getType() == MINUS) {
      irt0.setOp("MINUS");
    } else if(ast.getType() == MUL) {
      irt0.setOp("MUL");
    } else if(ast.getType() == DIV) {
      irt0.setOp("DIV");
    }

    arg((CommonTree)ast.getChild(0), irt1);
    arg((CommonTree)ast.getChild(1), irt2);
    irt.addSub(irt0);
    irt.addSub(irt1);
    irt.addSub(irt2);
  }

  private static void error(int tt)
  {
    System.out.println("IRT error: "+tokenNames[tt]);
    System.exit(1);
  }
}
