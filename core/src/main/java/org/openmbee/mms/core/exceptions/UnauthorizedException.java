package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends MMSException {

    public UnauthorizedException(Object body) {
        super(HttpStatus.UNAUTHORIZED, body);
    }

}
