package com.zinan.im.service.group.model.resp;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class AddMemberResp {

    private String memberId;

    /**
     * Adding result: 0 is success; 1 is failure; 2 is already a member of the group
     */
    private Integer result;

    private String resultMessage;
}
