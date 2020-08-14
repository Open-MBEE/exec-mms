package org.openmbee.sdvc.permissions.exceptions;

import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class PermissionException extends SdvcException {
    public PermissionException(HttpStatus status, Object body) {
        super(status, body);
    }
}