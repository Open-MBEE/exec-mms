package org.openmbee.sdvc.crud.controllers;

public class ErrorResponse extends BaseResponse {

    private String error;
    private int code;

    public String getError() {
        return error;
    }

    public ErrorResponse setError(String error) {
        this.error = error;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ErrorResponse setCode(int code) {
        this.code = code;
        return this;
    }
}
