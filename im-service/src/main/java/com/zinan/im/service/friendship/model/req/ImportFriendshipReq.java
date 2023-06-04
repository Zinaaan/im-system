package com.zinan.im.service.friendship.model.req;

import com.zinan.im.common.enums.FriendshipStatusEnum;
import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
public class ImportFriendshipReq extends RequestBase {

    @NotBlank(message = "fromId can not be null")
    private String fromId;

    @NotEmpty(message = "friendItem can not be null")
    @Size(min = 1, message = "At least one friend is required")
    private List<ImportFriendDto> friendItem;

    @Data
    public static class ImportFriendDto {

        private String toId;

        private String remark;

        private String addSource;

        // Default status 0:not added
        private Integer status = FriendshipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode();

        // Default black list status 1:normal
        private Integer black = FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode();
    }
}
