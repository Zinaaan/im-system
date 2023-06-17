package com.zinan.im.service.group.model.req;


import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class UpdateGroupReq extends RequestBase {

    @NotBlank(message = "Group id can not be null")
    private String groupId;

    private String groupName;

    private Integer mute;

    private Integer applyJoinType;

    private String introduction;

    private String notification;

    private String photo;

    private Integer maxMemberCount;

    private String extra;

}
