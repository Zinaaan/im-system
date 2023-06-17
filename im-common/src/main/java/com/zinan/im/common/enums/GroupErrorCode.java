package com.zinan.im.common.enums;

import com.zinan.im.common.exception.ApplicationExceptionsStrategy;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
public enum GroupErrorCode implements ApplicationExceptionsStrategy {

    GROUP_IS_NOT_EXIST(40000, "Group is not exist"),

    GROUP_IS_EXIST(40001, "Group is already exist"),

    GROUP_IS_HAVE_OWNER(40002, "This group already have a owner"),

    USER_IS_JOINED_GROUP(40003, "User is already joined this group"),

    USER_JOIN_GROUP_ERROR(40004, "Add new user to group failed"),

    GROUP_MEMBER_IS_BEYOND(40005, "The group member has reached its maximum"),

    MEMBER_IS_NOT_JOINED_GROUP(40006, "The user is not in the group"),

    THIS_OPERATE_NEED_ADMIN_ROLE(40007, "This operation is only available to group owners/admin"),

    THIS_OPERATE_NEED_APP_ADMIN_ROLE(40008, "This operation is only available to admin"),

    THIS_OPERATE_NEED_OWNER_ROLE(40009, "This operation is only available to owner"),

    GROUP_OWNER_IS_NOT_REMOVE(40010, "The group owner can not remove"),

    UPDATE_GROUP_BASE_INFO_ERROR(40011, "Update group information failed"),

    THIS_GROUP_IS_MUTE(40012, "This group is muted"),

    IMPORT_GROUP_ERROR(40013, "Import group data failed"),

    THIS_OPERATE_NEED_ONESELF(40014, "This operation only available to yourself"),

    PRIVATE_GROUP_CAN_NOT_DISSOLVE(40015, "The private group is not available to dissolve"),

    PUBLIC_GROUP_MUST_HAVE_OWNER(40016, "The public group must have a owner"),

    GROUP_MEMBER_IS_SPEAK(40017, "This group member is muted"),

    GROUP_IS_DISSOLVE(40018, "This group is already dissolved"),

    ;

    private int code;
    private String error;

    GroupErrorCode(int code, String error) {
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
