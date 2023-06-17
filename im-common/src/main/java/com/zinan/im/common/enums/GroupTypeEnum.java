package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
public enum GroupTypeEnum {

    /**
     * 群类型 1私有群（类似微信） 2公开群(类似qq）
     *
     * 1: private (like WeChat), 2: public
     */
    PRIVATE(1),

    PUBLIC(2),

    ;

    private final int code;

    GroupTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
