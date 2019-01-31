package org.openmbee.sdvc.crud.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends SdvcException {

    public BadRequestException(Object body) {
        super(HttpStatus.BAD_REQUEST, body);
    }
}
