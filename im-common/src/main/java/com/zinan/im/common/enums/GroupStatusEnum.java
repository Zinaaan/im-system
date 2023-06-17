package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
public enum GroupStatusEnum {

    /**
     * 1: normal, 2: dissolve, .... like forbidden
     */
    NORMAL(1),

    DISSOLVE(2),

    ;

    private final int code;

    GroupStatusEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
