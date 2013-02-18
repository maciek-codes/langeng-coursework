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
"NONE", "NONE", "NONE", "NONE", "BEGIN", "END", "WRITE", "WRITELN", "SEMICOLON", "OPENPAREN", "CLOSEPAREN", "INT", "EXPONENT", "REALNUM", "STRING", "COMMENT", "WS"};
  public static final int CLOSEPAREN=10;
  public static final int EXPONENT=12;
  public static final int WS=16;
  public static final int WRITELN=7;
  public static final int BEGIN=4;
  public static final int REALNUM=13;
  public static final int SEMICOLON=8;
  public static final int INT=11;
  public static final int OPENPAREN=9;
  public static final int END=5;
  public static final int COMMENT=15;
  public static final int WRITE=6;
  public static final int STRING=14;
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
      if (type.equals("real")) {
        irt.setOp("WRR");
        irt.addSub(irt1);
      }
      else {
        irt.setOp("WRS");
        irt.addSub(irt1);
      }
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
      irt.setOp("CONST");
      irt.addSub(irt1);
    }
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

  private static void error(int tt)
  {
    System.out.println("IRT error: "+tokenNames[tt]);
    System.exit(1);
  }
}
