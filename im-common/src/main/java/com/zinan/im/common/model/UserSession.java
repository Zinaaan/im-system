package com.zinan.im.common.model;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/07/04 15:47
 * @description User session entity
 */
@Data
public class UserSession {

    private String userId;

    private Integer appId;

    /**
     * PC, Mobile or others
     */
    private Integer clientType;

    /**
     * SDK version
     */
    private Integer version;

    /**
     * Connection status -> 1: online, 2: offline
     */
    private Integer connectionState;
}
