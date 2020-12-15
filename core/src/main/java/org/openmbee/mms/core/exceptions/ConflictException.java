package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends MMSException {

    public ConflictException(Object body) {
        super(HttpStatus.CONFLICT, body);
    }
}
