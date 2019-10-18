package org.openmbee.sdvc.core.exceptions;

import org.springframework.http.HttpStatus;

public class InternalErrorException extends SdvcException {

    public InternalErrorException(Object body) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, body);
    }
}