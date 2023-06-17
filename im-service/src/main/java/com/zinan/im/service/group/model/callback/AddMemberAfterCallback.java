package com.zinan.im.service.group.model.callback;

import com.zinan.im.service.group.model.resp.AddMemberResp;
import lombok.Data;

import java.util.List;


/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class AddMemberAfterCallback {
    private String groupId;
    private Integer groupType;
    private String operator;
    private List<AddMemberResp> memberId;
}
