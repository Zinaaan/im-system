package com.zinan.im.common.exception;

/**
 * @author lzn
 * @date 2023/05/30 16:11
 * @description
 */
public class ApplicationException extends RuntimeException {

    private final int code;

    private final String error;

    public ApplicationException(int code, String error) {
        super(error);
        this.code = code;
        this.error = error;
    }

    public ApplicationException(ApplicationExceptionsInterface exceptionsInterface) {
        super(exceptionsInterface.getError());
        this.code = exceptionsInterface.getCode();
        this.error = exceptionsInterface.getError();
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    /**
     * avoid the expensive and useless stack trace for api exceptions
     *
     * @see Throwable#fillInStackTrace()
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
