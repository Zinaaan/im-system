package com.zinan.im.service.group.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class GetJoinedGroupReq extends RequestBase {

    @NotBlank(message = "Member id cannot be empty")
    private String memberId;

    @NotEmpty(message = "Group type cannot be empty")
    @Size(min = 1, message = "At least one groupType is required")
    private List<Integer> groupType;

    /**
     * Number of groups in a single pull, if not filled in represents all groups
     */
    private Integer limit;

    private Integer offset;
}
