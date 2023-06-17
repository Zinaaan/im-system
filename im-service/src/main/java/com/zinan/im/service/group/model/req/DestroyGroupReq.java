package com.zinan.im.service.group.model.req;


import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class DestroyGroupReq extends RequestBase {

    @NotNull(message = "Group id cannot be empty")
    private String groupId;

}
