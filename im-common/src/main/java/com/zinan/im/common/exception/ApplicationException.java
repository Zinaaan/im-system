package com.zinan.im.common.exception;

import lombok.Getter;

/**
 * @author lzn
 * @date 2023/05/30 16:11
 * @description
 */
@Getter
public class ApplicationException extends RuntimeException {

    private final int code;

    private final String error;

    public ApplicationException(int code, String error) {
        super(error);
        this.code = code;
        this.error = error;
    }

    public ApplicationException(ApplicationExceptionsStrategy exceptionsInterface) {
        super(exceptionsInterface.getError());
        this.code = exceptionsInterface.getCode();
        this.error = exceptionsInterface.getError();
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
