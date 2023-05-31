package com.zinan.im.service.user.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
public class ModifyUserInfoReq extends RequestBase {

    @NotEmpty(message = "The userId can not be null")
    private String userId;

    private String nickName;

    private String location;

    private String birthDay;

    private String password;

    private String photo;

    private String userSex;

    private String selfSignature;

    /**
     * Verification mark for adding friends, 1: Verification required
     */
    private Integer friendAllowType;

    private String extra;
}