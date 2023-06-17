package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
public enum GroupMemberRoleEnum {

    /**
     * general member
     */
    ORDINARY(0),

    /**
     * admin
     */
    ADMIN(1),

    /**
     * owner
     */
    OWNER(2),

    /**
     * leave
     */
    LEAVE(3);
    ;

    private final int code;

    GroupMemberRoleEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
