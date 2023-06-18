package com.zinan.im.service.group.service;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.group.model.req.*;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
public interface ImGroupService {

    ResponseVO<?> importGroup(ImportGroupReq req);

    ResponseVO<?> updateGroupInfo(UpdateGroupReq req);

    ResponseVO<?> getGroupInfo(GetGroupReq req);

    ResponseVO<?> createGroup(CreateGroupReq req);

    ResponseVO<?> getJoinedGroup(GetJoinedGroupReq req);

    ResponseVO<?> destroyGroup(DestroyGroupReq req);

    ResponseVO<?> transferGroup(TransferGroupReq req);

    ResponseVO<?> getMemberJoinedGroup(GetJoinedGroupReq req);

    // Group member operations
    ResponseVO<?> importGroupMember(ImportGroupMemberReq req);

    ResponseVO<?> getRoleInGroup(String groupId, String memberId, Integer appId);

    ResponseVO<?> addGroupMember(AddGroupMemberReq req);

    ResponseVO<?> removeGroupMember(RemoveGroupMemberReq req);

    ResponseVO<?> updateGroupMember(UpdateGroupMemberReq req);
}
