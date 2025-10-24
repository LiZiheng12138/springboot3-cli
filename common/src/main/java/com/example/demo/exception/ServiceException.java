package com.example.demo.exception;

/**
 * 自定义业务异常
 */
public class ServiceException extends RuntimeException {

    private final int code;

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}