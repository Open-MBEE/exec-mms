package org.openmbee.mms.core.objects;

public class Rejection {

    private Object object;
    private int code;
    private String message;

    public Rejection() {}

    public Rejection(Object object, int code, String message) {
        this.object = object;
        this.code = code;
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
