package com.zinan.im.common.enums;

import com.zinan.im.common.exception.ApplicationExceptionsStrategy;

/**
 * @author lzn
 * @date 2023/05/31 18:04
 * @description
 */
public enum FriendShipErrorCode implements ApplicationExceptionsStrategy {

    IMPORT_SIZE_BEYOND(30000, "Imported quantity exceeds the limit"),

    ADD_FRIEND_ERROR(30001, "Adding friends failed"),

    TO_IS_YOUR_FRIEND(30002, "This person is already your friend"),

    TO_IS_NOT_YOUR_FRIEND(30003, "This person is not your friend"),

    FRIEND_IS_DELETED(30004, "Friend has been deleted"),

    FRIEND_IS_BLACK(30006, "Friend has been blacked out"),

    TARGET_IS_BLACK_YOU(30007, "Target person was blacked you out"),

    RELATIONSHIP_IS_NOT_EXIST(30008, "Relationship chain record does not exist"),

    ADD_BLACK_ERROR(30009, "Adding a blacklist failed"),

    FRIEND_IS_NOT_YOUR_BLACK(30010, "Friends are no longer on your blacklist"),

    NOT_APPROVER_OTHER_MAN_REQUEST(30011, "Unable to approve friend request from others"),

    FRIEND_REQUEST_IS_NOT_EXIST(30012, "Friend application does not exist"),

    FRIEND_SHIP_GROUP_CREATE_ERROR(30014, "Friend group creation failed"),

    FRIEND_SHIP_GROUP_IS_EXIST(30015, "Friend group already exists"),

    FRIEND_SHIP_GROUP_IS_NOT_EXIST(30016, "Friend group does not exist");

    private int code;
    private String error;

    FriendShipErrorCode(int code, String error) {
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
