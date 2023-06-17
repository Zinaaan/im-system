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
public class ImportGroupReq extends RequestBase {

    private String groupId;
    private String ownerId;

    private Integer groupType;

    @NotBlank(message = "Group name can not be empty")
    private String groupName;

    private Integer mute;

    private Integer applyJoinType;

    private String introduction;

    private String notification;

    private String photo;//群头像

    private Integer maxMemberCount;

    private Long createTime;

    private String extra;
}
