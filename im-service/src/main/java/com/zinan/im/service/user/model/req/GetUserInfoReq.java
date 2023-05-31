package com.zinan.im.service.user.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
public class GetUserInfoReq extends RequestBase {

    @NotNull(message = "The userIds can not be null")
    @Size(min = 1, message = "At least one user is required")
    private List<String> userIds;
}