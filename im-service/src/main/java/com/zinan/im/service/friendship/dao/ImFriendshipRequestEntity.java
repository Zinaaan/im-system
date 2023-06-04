package com.zinan.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/03 19:03
 * @description
 */
@Data
@TableName("im_friendship_request")
public class ImFriendshipRequestEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer appId;

    private String fromId;

    private String toId;

    private String remark;

    /**
     * Read or not, 1: read, 2: not read
     */
    private Integer readStatus;

    /**
     * Source of adding friends
     */
    private String addSource;

    private String addWording;

    /**
     * Approval stats, 1: approved, 2: denied
     */
    private Integer approveStatus;

    private Long createTime;

    private Long updateTime;

    private Long sequence;

}
