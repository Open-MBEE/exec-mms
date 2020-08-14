package org.openmbee.sdvc.core.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends SdvcException {

    public BadRequestException(Object body) {
        super(HttpStatus.BAD_REQUEST, body);
    }
}
