package com.sunnysuperman.commons.bean;

public class BeanException extends RuntimeException {
    private static final long serialVersionUID = 6437629501391399447L;

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
