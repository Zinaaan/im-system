package com.zinan.im.service.group.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class TransferGroupReq extends RequestBase {

    @NotEmpty(message = "Group id can not be empty")
    private String groupId;

    @NotEmpty(message = "Owner id can not be empty")
    private String ownerId;
}
