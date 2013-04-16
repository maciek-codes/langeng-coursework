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
    else {
      error(irt.getOp());
    }
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
}
