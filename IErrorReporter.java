import org.antlr.runtime.*;

public interface IErrorReporter {
    
    // Add variable to a list
    void addVariable(String variable);

    // Check if variable is in the list
    boolean hasVariable(String variable);

    // Report an error
    void reportError(String header, String msg, RecognitionException ex);

    // Report a warning
    void reportWarning(String msg, int line);

    void displayErrors();

    // Has any errors?
    boolean hasErrors();

}