package org.openmbee.sdvc.crud.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class InternalErrorException extends SdvcException {

    public InternalErrorException(Object body) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, body);
    }
}