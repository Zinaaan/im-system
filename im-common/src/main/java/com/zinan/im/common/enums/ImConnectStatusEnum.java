package com.zinan.im.common.enums;

/**
 * @author lzn
 * @date 2023/07/04 15:47
 * @description
 */
public enum ImConnectStatusEnum {

    /**
     * Connection status -> 1: online，2: offline
     */
    ONLINE_STATUS(1),

    OFFLINE_STATUS(2);

    private final Integer code;

    ImConnectStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
