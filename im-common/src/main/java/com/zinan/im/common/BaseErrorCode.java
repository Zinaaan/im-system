package com.zinan.im.common;

import com.zinan.im.common.exception.ApplicationExceptionsStrategy;

/**
 * @author lzn
 * @date 2023/05/30 16:21
 * @description
 */
public enum BaseErrorCode implements ApplicationExceptionsStrategy {

    /**
     * The response of the successful requests
     */
    SUCCESS(200, "success"),

    /**
     * The response of the internal error
     */
    SYSTEM_ERROR(90000, "Internal error, please contact the admin"),

    /**
     * The response of the parameter verification error
     */
    PARAMETER_ERROR(90001, "Parameter verification error");

    private final int code;

    private final String error;

    BaseErrorCode(int code, String error) {
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getError() {
        return this.error;
    }
}
