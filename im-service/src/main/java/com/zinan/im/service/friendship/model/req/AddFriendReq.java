package com.zinan.im.service.friendship.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
public class AddFriendReq extends RequestBase {

    @NotBlank(message = "fromId can not be null")
    private String fromId;

    @NotNull(message = "toItem can not be null")
    private FriendDto toItem;
}
