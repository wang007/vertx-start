package me.wang007.exception;

/**
 * created by wang007 on 2018/8/30
 */
public class NewInstanceException extends VertxStartException {

    public NewInstanceException() {
    }

    public NewInstanceException(String message) {
        super(message);
    }

    public NewInstanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NewInstanceException(Throwable cause) {
        super(cause);
    }

    public NewInstanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
