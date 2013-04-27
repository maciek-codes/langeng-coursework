// COMS22303: Code generation

import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import antlr.collections.AST;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Cg
{
  // Generate code from a program (in IRTree form)
  public static void program(IRTree irt, PrintStream o)
  {
    emit(o, "XOR R0,R0,R0");   // Initialize R0 to 0
    statement(irt, o);
    emit(o, "HALT");           // Program must end with HALT
    Memory.dumpData(o);        // Dump DATA lines: initial memory contents
  }

  // Generate code from a statement (in IRTree form)
  private static void statement(IRTree irt, PrintStream o)
  {
    if (irt.getOp().equals("SEQ")) {
      statement(irt.getSub(0), o);
      statement(irt.getSub(1), o);
    }
    else if (irt.getOp().equals("WRS") && irt.getSub(0).getOp().equals("MEM") &&
             irt.getSub(0).getSub(0).getOp().equals("CONST")) {
      String a = irt.getSub(0).getSub(0).getSub(0).getOp();
      emit(o, "WRS "+a);
    }
    else if (irt.getOp().equals("WRR")) {
      String e = expression(irt.getSub(0), o);
      emit(o, "WRR "+e);
      Reg.releaseLast();
    }
    // Assignment
    else if (irt.getOp().equals("MOVE")) {
      // Handle first variable's memory location
      String location = irt.getSub(0).getSub(0).getSub(0).getOp();
      // If we assign constant
      String e = expression(irt.getSub(1), o);
      emit(o, "STORE " + e + "," + "R0," + location);
      Reg.releaseLast();
    }
    else if (irt.getOp().equals("READ")) {
      // READ ( MEM ( CONST ...))
      String location = irt.getSub(0).getSub(0).getSub(0).getOp();
      String register = Reg.newReg();
      emit(o, "RDR " + register);
      emit(o, "STORE " + register + "," + "R0," + location);
      Reg.releaseLast();
    }
    else if (irt.getOp().equals("BINARYOP")) {
      expression(irt, o);
    }
    else if (irt.getOp().equals("LABEL")) {
      emit(o, irt.getSub(0).getOp()+":");
    }
    else if (irt.getOp().equals("REPEAT")) {
      //IRTree seq = irt.getSub(0);
      statement(irt.getSub(0), o);
      //statement(seq.getSub(1), o);
      //ifthen(seq.getSub(2), o);
    }
    else if (irt.getOp().equals("CJUMP")) {
      ifthen(irt, o);
    }
    else if (irt.getOp().equals("IFTHEN") || irt.getOp().equals("IFTHENELSE")) {
      ifthen(irt, o);
    } else if(irt.getOp().equals("JUMP")) {
      emit(o, "JMP " + irt.getSub(0).getSub(0).getOp());
    }
    else if (irt.getOp().equals("NOOP")) {
      // Skip..
    }
    else {
      error(irt.getOp());
    }
  }

  private static void ifthen(IRTree irt, PrintStream o)
  {
    // Should be CJUMP
    IRTree cJumpirt = irt.getSub(0);
    String lhs = null, rhs = null;

    lhs = expression(cJumpirt.getSub(1), o);
    rhs = expression(cJumpirt.getSub(2), o);

    if(cJumpirt.getSub(0).getOp().equals("MT")) {
      String reg = Reg.newReg();
      emit(o, "SUBR " + reg +"," + lhs + "," + rhs);
      emit(o, "BLTZR " + reg + "," + cJumpirt.getSub(4));
      emit(o, "JMP " + cJumpirt.getSub(3));

    } else if(cJumpirt.getSub(0).getOp().equals("LT")) {
      String reg = Reg.newReg();
      emit(o, "SUBR " + reg +"," + lhs + "," + rhs);
      emit(o, "BLTZR " + reg + "," + cJumpirt.getSub(3));
      emit(o, "JMP " + cJumpirt.getSub(4));
    } else if(cJumpirt.getSub(0).getOp().equals("MEQ")) {

      String reg = Reg.newReg();
      emit(o, "SUBR " + reg +"," + lhs + "," + rhs);
      emit(o, "BGEZR " + reg + "," + cJumpirt.getSub(3));
      emit(o, "JMP " + cJumpirt.getSub(4));

    } else if(cJumpirt.getSub(0).getOp().equals("LEQ")) {
      String reg = Reg.newReg();
      emit(o, "SUBR " + reg +"," + lhs + "," + rhs);
      emit(o, "BGEZR " + reg + "," + cJumpirt.getSub(4));
      emit(o, "JMP " + cJumpirt.getSub(3));

    } else if(cJumpirt.getSub(0).getOp().equals("EQ")) {
      String reg = Reg.newReg();
      emit(o, "SUBR " + reg +"," + lhs + "," + rhs);
      emit(o, "BEQZR " + reg + "," + cJumpirt.getSub(3));
      emit(o, "JMP " + cJumpirt.getSub(4));

    } else if(cJumpirt.getSub(0).getOp().equals("NEQ")) {
      String reg = Reg.newReg();
      emit(o, "SUBR " + reg +"," + lhs + "," + rhs);
      emit(o, "BNEZR " + reg + "," + cJumpirt.getSub(3));
      emit(o, "JMP " + cJumpirt.getSub(4));
    }

    Reg.releaseLast();  // LHS register
    Reg.releaseLast();  // RHS register
    Reg.releaseLast();  // B..R register
    
    statement(irt.getSub(1), o);
  }

  // Generate code from an expression (in IRTree form)
  private static String expression(IRTree irt, PrintStream o)
  {
    String result = "";
    if (irt.getOp().equals("CONST")) {
      String t = irt.getSub(0).getOp();
      result = Reg.newReg();
      emit(o, "MOVIR "+result+","+t);
    }
    else if(irt.getOp().equals("MEM") && irt.getSub(0).getOp().equals("CONST")) {
      String offset = irt.getSub(0).getSub(0).getOp();
      result = Reg.newReg();
      emit(o, "LOAD " + result + "," + "R0," + offset);
    }
    else if (irt.getOp().equals("BINARYOP")) {
      
      String operation = irt.getSub(0).getOp();

      if(irt.getSub(1).getOp() == "CONST" &&
        irt.getSub(2).getOp() != "CONST") {
        
        IRTree temp = irt.getSub(1);
        irt.setSub(1, irt.getSub(2));
        irt.setSub(2, temp);
      }

      if(operation == "PLUS") {
        result = expression(irt.getSub(1), o);
        if(irt.getSub(2).getOp() == "CONST") {
          String imm = irt.getSub(2).getSub(0).getOp();

          String temp = Reg.newReg();
          emit(o, "MOVIR " + temp + "," + imm);
          emit(o, "ADDR " + result + "," + result + "," + temp);
          Reg.releaseLast();
          
        } else {
          String right = expression(irt.getSub(2), o);
          emit(o, "ADDR " + result + "," + result + "," + right);
          Reg.releaseLast();
        }
      } else if(operation == "MINUS") {
        result = expression(irt.getSub(1), o);
        if(irt.getSub(2).getOp() == "CONST") {
          String imm = irt.getSub(2).getSub(0).getOp();
          
          String temp = Reg.newReg();
          emit(o, "MOVIR " + temp + "," + imm);
          emit(o, "SUBR " + result + "," + result + "," + temp);
          Reg.releaseLast();

        } else {
          String right = expression(irt.getSub(2), o);
          emit(o, "SUBR " + result + "," + result + "," + right);
          Reg.releaseLast();
        }
      } else if(operation == "DIV") {
        result = expression(irt.getSub(1), o);
        if(irt.getSub(2).getOp() == "CONST") {
          String imm = irt.getSub(2).getSub(0).getOp();
          
          String temp = Reg.newReg();
          emit(o, "MOVIR " + temp + "," + imm);
          emit(o, "DIVR " + result + "," + result + "," + temp);
          Reg.releaseLast();
        
        } else {
          String right = expression(irt.getSub(2), o);
          emit(o, "DIVR " + result + "," + result + "," + right);
          Reg.releaseLast();
        }
      } else if(operation == "MUL") {
        result = expression(irt.getSub(1), o);
        if(irt.getSub(2).getOp() == "CONST") {
          String imm = irt.getSub(2).getSub(0).getOp();
          String temp = Reg.newReg();
          emit(o, "MOVIR " + temp + "," + imm);
          emit(o, "MULR " + result + "," + result + "," + temp);
          Reg.releaseLast();
        } else {
          String right = expression(irt.getSub(2), o);
          emit(o, "MULR " + result + "," + result + "," + right);
          Reg.releaseLast();
        }
      }
    }
    else {
      error(irt.getOp());
    }
    return result;  // Return name of the register holding expression's value
  }

  // Generate an instruction
  private static void emit(PrintStream o, String s)
  {
    o.println(s);
  }

  // Error
  private static void error(String op)
  {
    System.out.println("CG error: "+op);
    System.exit(1);
  }

  private static boolean isInteger(String str) {
    
    if(str == null || str.length() == 0)
      return false;

    if(str.matches("^(\\+|\\-)?\\d*(\\.0+)*"))
      return true;

    return false;
  }

  private static String getIntegerPart(String realInput) {

    if(!isInteger(realInput))
      return null;

    String[] result = realInput.split("\\.");

    return result[0];
  }
}
