package com.zinan.im.service.group.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class UpdateGroupMemberReq extends RequestBase {

    @NotEmpty(message = "Group id can not be empty")
    private String groupId;

    @NotEmpty(message = "memberId id can not be empty")
    private String memberId;

    private String alias;

    private Integer role;

    private String extra;

}
