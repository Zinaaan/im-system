package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
public enum FriendShipStatusEnum {

    /**
     * 0:Not added 1:Added 2:Deleted
     */
    FRIEND_STATUS_NO_FRIEND(0),

    FRIEND_STATUS_NORMAL(1),

    FRIEND_STATUS_DELETE(2),

    /**
     * 0:Not added 1:Black out 2:Deleted
     */
    BLACK_STATUS_NO_BLACKED(0),

    BLACK_STATUS_NORMAL(1),

    BLACK_STATUS_DELETE(2),
    ;

    private final int code;

    FriendShipStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
