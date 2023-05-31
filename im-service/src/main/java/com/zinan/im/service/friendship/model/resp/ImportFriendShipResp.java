package com.zinan.im.service.friendship.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@Data
public class ImportFriendShipResp {

    private List<String> successId;

    private List<String> errorId;
}
