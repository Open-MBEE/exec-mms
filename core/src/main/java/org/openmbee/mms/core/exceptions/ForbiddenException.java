package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends MMSException {

    public ForbiddenException(Object body) {
        super(HttpStatus.FORBIDDEN, body);
    }

}
