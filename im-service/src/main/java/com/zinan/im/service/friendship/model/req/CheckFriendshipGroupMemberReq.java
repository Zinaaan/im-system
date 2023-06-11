package com.zinan.im.service.friendship.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author lzn
 * @date 2023/06/06 09:57
 * @description
 */
@Data
public class CheckFriendshipGroupMemberReq extends RequestBase {

    @NotEmpty(message = "fromId can not be null")
    private String fromId;

    @NotEmpty(message = "groupName can not be null")
    private String groupName;
}
