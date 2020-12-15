package org.openmbee.mms.core.exceptions;

import org.springframework.http.HttpStatus;

public abstract class MMSException extends RuntimeException {

    private HttpStatus code;
    private Object messageObject;

    public MMSException() {
    }

    public MMSException(HttpStatus code, Object messageObject) {
        this.code = code;
        this.messageObject = messageObject;
    }

    public MMSException(int code, Object messageObject) {
        this.code = HttpStatus.resolve(code);
        this.messageObject = messageObject;
    }

    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    public Object getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;
    }

}