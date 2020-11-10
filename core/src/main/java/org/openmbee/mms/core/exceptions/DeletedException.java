package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class DeletedException extends SdvcException {

    public DeletedException(Object body) {
        super(HttpStatus.GONE, body);
    }
}
