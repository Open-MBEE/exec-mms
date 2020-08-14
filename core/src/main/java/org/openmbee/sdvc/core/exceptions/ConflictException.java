package org.openmbee.sdvc.core.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends SdvcException {

    public ConflictException(Object body) {
        super(HttpStatus.CONFLICT, body);
    }
}
