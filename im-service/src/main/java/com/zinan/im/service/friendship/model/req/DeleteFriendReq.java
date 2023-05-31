package com.zinan.im.service.friendship.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
public class DeleteFriendReq extends RequestBase {

    @NotBlank(message = "fromId can not be null")
    private String fromId;

    @NotBlank(message = "toId can not be null")
    private String toId;

}
