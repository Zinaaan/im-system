package com.zinan.im.service.friendship.service;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.friendship.model.req.*;

/**
 * @author lzn
 * @date 2023/06/04 12:49
 * @description
 */
public interface ImFriendshipGroupService {

    ResponseVO<?> addGroup(AddFriendshipGroupReq req);

    ResponseVO<?> deleteGroup(DeleteFriendshipGroupReq req);

    ResponseVO<?> getGroup(CheckFriendshipGroupMemberReq req);

    ResponseVO<?> addGroupMember(AddFriendshipGroupMemberReq req);

    ResponseVO<?> delSpecGroupMember(DeleteFriendshipGroupMemberReq req);

    ResponseVO<?> delAllGroupMember(Long req);
}
