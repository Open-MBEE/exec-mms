package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends SdvcException {

    public BadRequestException(Object body) {
        super(HttpStatus.BAD_REQUEST, body);
    }
}
