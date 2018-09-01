package org.wang007.exception;

/**
 * created by wang007 on 2018/8/25
 */
public class VertxStartException extends RuntimeException {

    public VertxStartException() {
    }

    public VertxStartException(String message) {
        super(message);
    }

    public VertxStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public VertxStartException(Throwable cause) {
        super(cause);
    }

    public VertxStartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
