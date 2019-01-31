package org.openmbee.sdvc.crud.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class NotModifiedException extends SdvcException {

    public NotModifiedException(Object body) {
        super(HttpStatus.NOT_MODIFIED, body);
    }
}
