package com.zinan.im.service.group.model.resp;

import com.zinan.im.service.group.model.req.GroupMemberDto;
import lombok.Data;

import java.util.List;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class GetGroupResp {

    private String groupId;

    private String ownerId;

    private Integer groupType;

    private String groupName;

    private Integer mute;

    private Integer applyJoinType;

    private Integer privateChat;

    private String introduction;

    private String notification;

    private String photo;

    private Integer maxMemberCount;

    private Integer status;

    private List<GroupMemberDto> memberList;
}
