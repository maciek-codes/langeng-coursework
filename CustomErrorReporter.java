import org.antlr.runtime.*;
import java.util.*;


public class CustomErrorReporter implements IErrorReporter {
    
    private int errorCounter = 0;
    private int warningCount = 0;

    private List<String> vars = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private List<String> warnings = new ArrayList<String>();

    // Report an erro
    public void reportError(String header, String msg, RecognitionException e) {

        // Add error message to a queue
        errors.add("Error in line " + e.line +
            " at pos " + e.index + ": " + msg);

        errorCounter++;
    }

    // Add variable to a list
    public void addVariable(String variable) {
        if(hasVariable(variable) || variable == null || variable.length() <= 0)
            return;

        vars.add(variable);
    }

    // Check if variable is in the list
    public boolean hasVariable(String variable) {
        if(variable == null || variable.length() <= 0)
            return false;

        return vars.contains(variable);
    }

    // Has any errors?
    public boolean hasErrors() {
        return errorCounter > 0;
    }

    // Report a warning
    public void reportWarning(String msg, int line) {
        // Add warning message to the lsit
        warnings.add("Warning in line " + line +
            ": " + msg);

        warningCount++;
    }

    public void displayErrors() {

        // Display errors
        for(String err : errors) 
        {
            System.err.println(err);
        }
        
        // Diplay all warnings
        for(String warn : warnings) 
        {
            System.err.println(warn);
        }

    }

}