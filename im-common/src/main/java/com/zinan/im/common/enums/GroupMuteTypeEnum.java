package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
public enum GroupMuteTypeEnum {

    /**
     * Mute all, 0: normal, 1: mute
     */
    NOT_MUTE(0),


    MUTE(1),

    ;


    private final int code;

    GroupMuteTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
