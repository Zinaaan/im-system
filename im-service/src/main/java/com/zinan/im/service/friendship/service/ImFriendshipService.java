package com.zinan.im.service.friendship.service;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.friendship.model.req.*;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
public interface ImFriendshipService {

    ResponseVO<?> importFriendship(ImportFriendshipReq req);

    ResponseVO<?> addFriend(AddFriendReq req);

    ResponseVO<?> updateFriend(UpdateFriendReq req);

    ResponseVO<?> deleteFriend(DeleteFriendReq req);

    ResponseVO<?> deleteAllFriend(DeleteFriendReq req);

    ResponseVO<?> getFriendship(GetRelationReq req);

    ResponseVO<?> getAllFriendship(GetAllFriendshipReq req);

    ResponseVO<?> checkFriendship(CheckFriendshipReq req);

    ResponseVO<?> addToBlackList(AddFriendshipBlackReq req);

    ResponseVO<?> deleteFromBlackList(DeleteBlackReq req);

    ResponseVO<?> checkIfInBlackList(CheckFriendshipReq req);

    ResponseVO<?> addFriendshipRequest(AddFriendReq req);

    ResponseVO<?> approveFriendRequest(ApproverFriendRequestReq req);

    ResponseVO<?> getFriendshipRequest(GetFriendshipRequestReq req);

    ResponseVO<?> readFriendshipRequest(ReadFriendshipRequestReq req);
}
