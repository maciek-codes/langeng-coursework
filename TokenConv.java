import java.lang.reflect.Array;
import java.io.*;
import java.util.*;

/**
 * Tokens converter.
 *
 */
public class TokenConv
{

  public static void main(String[] args)
  {
    String tokensFile = args[0];
    String progFile = args[1];
    String tempFile = "CAMLE_TEMP.txt";
    List<String> tokenName = new ArrayList<String>();
    List<Integer> tokenType = new ArrayList<Integer>();
    List<String> nameOf = new ArrayList<String>();
    String line;
    boolean skipping = false;
    try {
      int nTokens = readTokensFile(tokensFile, tokenName, tokenType, nameOf);
      BufferedReader in = new BufferedReader(new FileReader(progFile));
      PrintStream o = new PrintStream(new FileOutputStream(tempFile));
      while ((line = in.readLine()) != null) {
        if (line.equals("// CAMLE TOKENS END")) {
          skipping = false;
        }
        if (!skipping) {
          o.println(line);
        }
        if (line.equals("// CAMLE TOKENS BEGIN")) {
          skipping = true;
          writeTokensCode(o, nTokens, tokenName, tokenType, nameOf);
        }
      }
      in.close();
      o.close();
      new File(tempFile).renameTo(new File(progFile));
    }
    catch (Exception e) {
      System.out.println(e.toString());
      System.exit(1);
    }
  }

  private static void writeTokensCode(PrintStream o, int nTokens, List<String> tokenName,
                      List<Integer> tokenType, List<String> nameOf) throws Exception
  {
    int i;
    o.println("  public static final String[] tokenNames = new String[] {");
    o.print("\""+nameOf.get(0)+"\"");
    for (i=1; i<nameOf.size(); i++) {
      o.print(", \""+nameOf.get(i)+"\"");
    }
    o.println("};");
    for (i=0; i<nTokens; i++) {
      o.println("  public static final int "+tokenName.get(i)+"="+tokenType.get(i)+";");
    }
  }

  private static int readTokensFile(String tokensFile, List<String> tokenName,
                     List<Integer> tokenType, List<String> nameOf) throws Exception
  {
    String line, name;
    String[] part;
    int nTokens = 0;
    int type, i;
    BufferedReader in = new BufferedReader(new FileReader(tokensFile));
    while ((line = in.readLine()) != null) {
      part = line.split("=");
      name = part[0];
      type = Integer.parseInt(part[1]);
      tokenName.add(name);
      tokenType.add(type);
      while (nameOf.size() <= type) {
        nameOf.add("NONE");
      }
      nameOf.set(type, name);
      nTokens++;
    }
    in.close();
    return nTokens;
  }
}
