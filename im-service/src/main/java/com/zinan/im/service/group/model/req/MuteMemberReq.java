package com.zinan.im.service.group.model.req;


import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class MuteMemberReq extends RequestBase {

    @NotBlank(message = "Group id can not be null")
    private String groupId;

    @NotBlank(message = "MemberId can not be null")
    private String memberId;

    /**
     * Banning time, unit is milliseconds
     */
    @NotNull(message = "Mute time can not be null")
    private Long speakDate;
}
