package com.zinan.im.common.enums;

public enum ApproverFriendRequestStatusEnum {

    /**
     * 1 agree；2 reject。
     */
    AGREE(1),

    REJECT(2),
    ;

    private final int code;

    ApproverFriendRequestStatusEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
