package me.wang007.exception;

/**
 *
 * created by wang007 on 2018/9/9
 */
public class InitialException extends VertxStartException {

    public InitialException() {
    }

    public InitialException(String message) {
        super(message);
    }

    public InitialException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitialException(Throwable cause) {
        super(cause);
    }

    public InitialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
