package com.zinan.im.service.group.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class CreateGroupReq extends RequestBase {

    private String groupId;

    private String ownerId;

    private Integer groupType;

    private String groupName;

    /**
     * Whether to ban the whole team, 0 no ban; 1 full ban.
     */
    private Integer mute;

    /**
     * Join group permission, 0 Everyone can join; 1 Group members can pull people; 2 Group administrator or group can pull people.
     */
    private Integer applyJoinType;

    private String introduction;

    private String notification;

    private String photo;

    private Integer maxMemberCount;

    private List<GroupMemberDto> member;

    private String extra;
}
