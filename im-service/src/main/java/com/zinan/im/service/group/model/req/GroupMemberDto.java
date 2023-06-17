package com.zinan.im.service.group.model.req;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */

@Data
public class GroupMemberDto {

    private String memberId;

    private String alias;

    /**
     * Group member type, 0 common member, 1 administrator, 2 group owner, 3 removed members,
     * when modifying group member information, can only take the value of 0/1, other values by other interfaces to achieve, does not support 3
     */
    private Integer role;

//    private Integer speakFlag;

    private Long speakDate;

    private String joinType;

    private Long joinTime;

}
