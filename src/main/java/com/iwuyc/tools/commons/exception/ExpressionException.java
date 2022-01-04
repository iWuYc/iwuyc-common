package com.iwuyc.tools.commons.exception;

public class ExpressionException extends RuntimeException {
    public ExpressionException(String message) {
        super(message);
    }

    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
