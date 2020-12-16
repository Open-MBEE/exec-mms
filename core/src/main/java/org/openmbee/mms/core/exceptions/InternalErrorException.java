package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class InternalErrorException extends MMSException {

    public InternalErrorException(Object body) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, body);
    }
}