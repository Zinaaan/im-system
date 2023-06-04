package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/06/1 10:11
 * @description
 */
public enum CheckFriendshipTypeEnum {

    /**
     * 1: one-side verification, 2: two-side verification。
     */
    SINGLE(1),

    BOTH(2),
    ;

    private final int type;

    CheckFriendshipTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
