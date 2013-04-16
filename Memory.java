import java.io.*;
import java.util.*;

public class Memory {

  static HashMap<String, Integer> variables = new HashMap<String, Integer>();

  static ArrayList<Byte> memory = new ArrayList<Byte>();

  static public int allocateString(String text)
  {
    int addr = memory.size();
    int size = text.length();
    for (int i=0; i<size; i++) {
      memory.add(new Byte("", text.charAt(i)));
    }
    memory.add(new Byte("", 0));
    return addr;
  }

  static public int getVariable(String variable)
  {
    // If variable was there
    if(variables.containsKey(variable)) {
      return variables.get(variable);
    }

    // Make sure address starts as a multiple of 4
    while(memory.size() % 4 != 0) {
      memory.add(new Byte("", 0));
    }

    // Address
    int addr = memory.size();

    // Allocate 4-bytes for a real
    for (int i=0; i < 4; i++) {
      memory.add(new Byte(variable, 0));
    }

    variables.put(variable, addr);
    return addr;
  }

  static public void dumpData(PrintStream o)
  {
    Byte b;
    String s;
    int c;
    int size = memory.size();
    for (int i=0; i<size; i++) {
      b = memory.get(i);
      c = b.getContents();
      if (c >= 32) {
        s = String.valueOf((char)c);
      }
      else {
        s = ""; // "\\"+String.valueOf(c);
      }
      o.println("DATA "+c+" ; "+s+" "+b.getName());
    }
  }
}

class Byte {
  String varname;
  int contents;

  Byte(String n, int c)
  {
    varname = n;
    contents = c;
  }

  String getName()
  {
    return varname;
  }

  int getContents()
  {
    return contents;
  }
}
