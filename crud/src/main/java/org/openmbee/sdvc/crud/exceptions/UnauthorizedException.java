package org.openmbee.sdvc.crud.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends SdvcException {

    public UnauthorizedException(Object body) {
        super(HttpStatus.UNAUTHORIZED, body);
    }

}
