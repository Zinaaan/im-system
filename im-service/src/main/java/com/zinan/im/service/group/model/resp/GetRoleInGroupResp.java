package com.zinan.im.service.group.model.resp;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class GetRoleInGroupResp {

    private Long groupMemberId;

    private String memberId;

    private Integer role;

    private Long speakDate;

}
