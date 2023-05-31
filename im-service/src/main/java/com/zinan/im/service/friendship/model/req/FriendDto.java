package com.zinan.im.service.friendship.model.req;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
public class FriendDto {

    private String toId;

    private String remark;

    private String addSource;

    private String extra;

    private String addWording;
}
