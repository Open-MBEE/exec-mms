package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public class NotModifiedException extends SdvcException {

    public NotModifiedException(Object body) {
        super(HttpStatus.NOT_MODIFIED, body);
    }
}
