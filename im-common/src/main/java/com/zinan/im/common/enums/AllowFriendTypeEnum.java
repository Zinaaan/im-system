package com.zinan.im.common.enums;

public enum AllowFriendTypeEnum {

    /**
     * Verification required
     */
    NEED(2),

    /**
     * No verification required
     */
    NOT_NEED(1),
    ;

    private final int code;

    AllowFriendTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
