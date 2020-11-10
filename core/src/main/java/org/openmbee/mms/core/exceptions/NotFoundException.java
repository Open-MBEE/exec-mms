package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends SdvcException {

    public NotFoundException(Object body) {
        super(HttpStatus.NOT_FOUND, body);
    }
}
