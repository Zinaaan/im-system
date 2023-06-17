package com.zinan.im.service.group.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
@TableName("im_group")
public class ImGroupEntity {

    @TableId(value = "group_id")
    private String groupId;

    private Integer appId;

    private String ownerId;

    private Integer groupType;

    private String groupName;

    private Integer mute;

    /**
     * The options for applying to join a group include the following:
     *  0 means no one is allowed to apply to join
     *  1 means approval by the group owner or administrator is required
     *  2 means that you can join the group freely without approval
     */
    private Integer applyJoinType;

    private String introduction;

    private String notification;

    private String photo;

    private Integer maxMemberCount;

    private Integer status;

    private Long sequence;

    private Long createTime;

    private Long updateTime;

    private String extra;
}
