package errorhandling;

import javax.ws.rs.WebApplicationException;
import java.util.List;

public class ValidationException extends WebApplicationException {

    public ValidationException (String message) {
        super(message, 400);
    }

    public ValidationException (String message, List<String> errors){
        this(message + ": " + String.join("; ",errors));
    }
}