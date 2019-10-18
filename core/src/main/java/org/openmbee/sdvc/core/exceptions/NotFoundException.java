package org.openmbee.sdvc.core.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends SdvcException {

    public NotFoundException(Object body) {
        super(HttpStatus.NOT_FOUND, body);
    }
}
