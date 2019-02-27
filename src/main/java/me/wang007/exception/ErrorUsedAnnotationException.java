package me.wang007.exception;

/**
 * created by wang007 on 2018/8/25
 */
public class ErrorUsedAnnotationException extends VertxStartException {

    public ErrorUsedAnnotationException() {
    }

    public ErrorUsedAnnotationException(String message) {
        super(message);
    }

    public ErrorUsedAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorUsedAnnotationException(Throwable cause) {
        super(cause);
    }

    public ErrorUsedAnnotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
