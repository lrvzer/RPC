package io.rpc.common.exception;

/**
 * 自定义异常类
 */
public class SerializerException extends RuntimeException {

    private static final long serialVersionUID = -4741206756418554575L;

    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializerException(Throwable cause) {
        super(cause);
    }

}
