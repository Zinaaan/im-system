package com.zinan.im.service.friendship.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
public class CheckFriendshipReq extends RequestBase {

    @NotBlank(message = "fromId can not be null")
    private String fromId;

    @NotEmpty(message = "toIds can not be null")
    @Size(min = 1, message = "At least one friend id is required")
    private List<String> toIds;

    @NotNull(message = "checkType can not be null")
    private Integer checkType;
}
