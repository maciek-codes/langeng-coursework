import org.antlr.runtime.*;


public class CustomErrorReporter implements IErrorReporter {
    
    public void reportError(String header, String msg, RecognitionException e) {
        System.err.println("Error in line " + e.line +
        	" at pos " + e.index + ": " + msg);
    }

}