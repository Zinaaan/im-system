package com.zinan.im.common.enums;

import com.zinan.im.common.exception.ApplicationExceptionsStrategy;

/**
 * @author lzn
 * @date 2023/05/31 10:11
 * @description
 */
public enum UserErrorCode implements ApplicationExceptionsStrategy {

    IMPORT_SIZE_BEYOND(20000, "The number of imports has exceeded the limit"),
    USER_IS_NOT_EXIST(20001, "User does not exist"),
    SERVER_GET_USER_ERROR(20002, "Service fail to aquire users"),
    MODIFY_USER_ERROR(20003, "Updating failure"),
    DUPLICATED_USER(20004, "Duplicated user"),
    SERVER_NOT_AVAILABLE(71000, "No available services"),
    ;

    private int code;
    private String error;

    UserErrorCode(int code, String error) {
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