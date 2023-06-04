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
public class ApproverFriendRequestReq extends RequestBase {

    @NotNull(message = "id can not be null")
    private Long id;

    /**
     * 1 approve 2 deny
     */
    @NotNull(message = "status can not be null")
    private Integer status;
}
