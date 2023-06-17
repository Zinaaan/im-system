package com.zinan.im.service.group.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class AddGroupMemberReq extends RequestBase {

    @NotBlank(message = "Group id cannot be empty")
    private String groupId;

    @NotEmpty(message = "Group members cannot be empty")
    @Size(min = 1, message = "At least one member is required")
    private List<GroupMemberDto> members;

}
