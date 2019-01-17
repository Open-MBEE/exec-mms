package org.openmbee.sdvc.crud.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class ConflictException extends SdvcException {

    public ConflictException(Object body) {
        super(HttpStatus.CONFLICT, body);
    }
}
