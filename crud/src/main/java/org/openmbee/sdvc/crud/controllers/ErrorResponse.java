package org.openmbee.sdvc.crud.controllers;

public class ErrorResponse extends BaseResponse {

    private String error;
    private int code;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
