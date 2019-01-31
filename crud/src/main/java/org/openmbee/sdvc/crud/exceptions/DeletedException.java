package org.openmbee.sdvc.crud.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class DeletedException extends SdvcException {

    public DeletedException(Object body) {
        super(HttpStatus.GONE, body);
    }
}
