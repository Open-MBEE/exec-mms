package exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class TwcPermissionException extends SdvcException {
    public TwcPermissionException(HttpStatus status, Object body) {
        super(status, body);
    }
}
