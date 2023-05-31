package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/05/31 10:11
 * @description
 */
public enum DelFlagEnum {

    /**
     * 0 normal；1 delete。
     */
    NORMAL(0),

    DELETE(1),
    ;

    private final int code;

    DelFlagEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}