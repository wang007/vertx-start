package me.wang007.exception;

/**
 * created by wang007 on 2018/8/27
 */
public class CreateComponentDescriptionExceptioin extends VertxStartException {

    public CreateComponentDescriptionExceptioin() {
    }

    public CreateComponentDescriptionExceptioin(String message) {
        super(message);
    }

    public CreateComponentDescriptionExceptioin(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateComponentDescriptionExceptioin(Throwable cause) {
        super(cause);
    }

    public CreateComponentDescriptionExceptioin(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
