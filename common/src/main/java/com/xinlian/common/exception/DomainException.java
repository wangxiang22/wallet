package com.xinlian.common.exception;

public class DomainException extends RuntimeException {

    private String errorCode;

    public DomainException() {
        super("系统繁忙,请稍后再试");
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
