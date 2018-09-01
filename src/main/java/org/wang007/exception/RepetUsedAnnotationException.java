package org.wang007.exception;

/**
 * created by wang007 on 2018/8/25
 */
public class RepetUsedAnnotationException extends VertxStartException {

    public RepetUsedAnnotationException() {
    }

    public RepetUsedAnnotationException(String message) {
        super(message);
    }

    public RepetUsedAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepetUsedAnnotationException(Throwable cause) {
        super(cause);
    }

    public RepetUsedAnnotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
