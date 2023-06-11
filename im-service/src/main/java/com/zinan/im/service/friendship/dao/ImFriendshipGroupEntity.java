package com.zinan.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/04 12:55
 * @description
 */
@Data
@TableName("im_friendship_group")
public class ImFriendshipGroupEntity {

    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    private String fromId;

    private Integer appId;

    private String groupName;

    private Long createTime;

    private Long updateTime;

    private Long sequence;

    private int delFlag;
}
