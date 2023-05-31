package com.zinan.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.AutoMap;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
@TableName("im_friendship")
@AutoMap
public class ImFriendShipEntity {

    @TableField(value = "app_id")
    private Integer appId;

    @TableField(value = "from_id")
    private String fromId;

    @TableField(value = "to_id")
    private String toId;
    private String remark;
    /**
     * 1: normal, 2: deleted
     */
    private Integer status;
    /**
     * 1: normal, 2: black out
     */
    private Integer black;

    private Long createTime;

    /**
     * Sequence of friendship
     */
    private Long friendSequence;

    /**
     * Sequence of blacklist relationship
     */
    private Long blackSequence;

    /**
     * Resource of friend
     */
    private String addSource;

    private String extra;
}
