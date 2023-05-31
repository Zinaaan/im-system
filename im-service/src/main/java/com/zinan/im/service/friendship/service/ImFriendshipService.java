package com.zinan.im.service.friendship.service;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.friendship.model.req.AddFriendReq;
import com.zinan.im.service.friendship.model.req.ImportFriendShipReq;
import com.zinan.im.service.friendship.model.req.UpdateFriendReq;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
public interface ImFriendshipService {

    ResponseVO<?> importFriendShip(ImportFriendShipReq req);

    ResponseVO<?> addFriend(AddFriendReq req);

    ResponseVO<?> updateFriend(UpdateFriendReq req);
}
