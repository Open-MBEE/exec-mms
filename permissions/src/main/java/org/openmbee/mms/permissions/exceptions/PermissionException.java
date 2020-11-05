package org.openmbee.mms.permissions.exceptions;

import org.openmbee.mms.core.exceptions.SdvcException;
import org.springframework.http.HttpStatus;

public class PermissionException extends SdvcException {
    public PermissionException(HttpStatus status, Object body) {
        super(status, body);
    }
}