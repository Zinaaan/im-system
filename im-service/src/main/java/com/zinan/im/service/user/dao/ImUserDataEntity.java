package com.zinan.im.service.user.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
@TableName("im_user_data")
public class ImUserDataEntity {

    private String userId;

    private String nickName;

    private String location;

    private String birthDay;

    private String password;

    private String photo;

    private Integer userSex;

    private String selfSignature;

    /**
     * Whether verification required for adding friends, 1: required, 2: non-required
     * 加好友验证类型（Friend_AllowType） 1需要验证
     */
    private Integer friendAllowType;

    /**
     * Adding friends, 0: allowed, 1: disabled
     * 管理员禁止用户添加加好友：0 未禁用 1 已禁用
     */
    private Integer disableAddFriend;

    /**
     * Forbidden sign, 0: allowed,  1: disabled
     * 禁用标识(0 未禁用 1 已禁用)
     */
    private Integer forbiddenFlag;

    /**
     * Ban sign (禁言标识)
     */
    private Integer silentFlag;
    /**
     * Use type, 1: normal user, 2: customer service, 3: robot
     */
    private Integer userType;

    private Integer appId;

    private Integer delFlag;

    private String extra;

}