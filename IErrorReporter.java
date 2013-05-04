import org.antlr.runtime.*;

public interface IErrorReporter {
    
    void reportError(String header, String msg, RecognitionException ex);

}