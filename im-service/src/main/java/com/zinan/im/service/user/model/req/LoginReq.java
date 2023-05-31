package com.zinan.im.service.user.model.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
public class LoginReq {

    @NotNull(message = "The user id can not be null")
    private String userId;

    @NotNull(message = "The app id can not be null")
    private Integer appId;

    private Integer clientType;

}