package com.zinan.im.service.friendship.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
public class ApproverFriendRequestReq extends RequestBase {

    private Long id;

    /**
     * 1 agree 2 deny
     */
    private Integer status;
}
