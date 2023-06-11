package com.zinan.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.enums.DelFlagEnum;
import com.zinan.im.common.enums.FriendshipErrorCode;
import com.zinan.im.service.friendship.dao.ImFriendShipGroupMemberEntity;
import com.zinan.im.service.friendship.dao.ImFriendshipGroupEntity;
import com.zinan.im.service.friendship.dao.mapper.ImFriendshipGroupMapper;
import com.zinan.im.service.friendship.dao.mapper.ImFriendshipGroupMemberMapper;
import com.zinan.im.service.friendship.model.req.*;
import com.zinan.im.service.friendship.service.ImFriendshipGroupService;
import com.zinan.im.service.user.model.req.UserId;
import com.zinan.im.service.user.service.ImUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzn
 * @date 2023/06/04 12:52
 * @description
 */
@Service
public class ImFriendshipGroupServiceImpl implements ImFriendshipGroupService {

    private final ImUserService imUserService;

    private final ImFriendshipGroupMapper imFriendshipGroupMapper;

    private final ImFriendshipGroupMemberMapper imFriendShipGroupMemberMapper;

    public ImFriendshipGroupServiceImpl(ImUserService imUserService, ImFriendshipGroupMapper imFriendshipGroupMapper,
                                        ImFriendshipGroupMemberMapper imFriendShipGroupMemberMapper) {
        this.imUserService = imUserService;
        this.imFriendshipGroupMapper = imFriendshipGroupMapper;
        this.imFriendShipGroupMemberMapper = imFriendShipGroupMemberMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> addGroup(AddFriendshipGroupReq req) {

        Integer appId = req.getAppId();
        String groupName = req.getGroupName();
        String fromId = req.getFromId();
        int delFlag = DelFlagEnum.NORMAL.getCode();
        List<String> toIds = req.getToIds();

        QueryWrapper<ImFriendshipGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("group_name", groupName);
        queryWrapper.eq("from_id", fromId);
        queryWrapper.eq("del_flag", delFlag);

        ImFriendshipGroupEntity entity = imFriendshipGroupMapper.selectOne(queryWrapper);
        if (entity != null) {
            return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        ImFriendshipGroupEntity insert = new ImFriendshipGroupEntity();
        insert.setFromId(fromId);
        insert.setAppId(appId);
        insert.setGroupName(groupName);
        insert.setCreateTime(System.currentTimeMillis());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());

        int dbResult = imFriendshipGroupMapper.insert(insert);
        if (dbResult != 1) {
            return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
        }

        if (toIds != null && toIds.size() > 0) {
            AddFriendshipGroupMemberReq groupMemberReq = new AddFriendshipGroupMemberReq();
            groupMemberReq.setFromId(fromId);
            groupMemberReq.setGroupName(groupName);
            groupMemberReq.setToIds(toIds);
            groupMemberReq.setAppId(appId);
            groupMemberReq.setOperator(fromId);

            ResponseVO<?> responseVO = addGroupMember(groupMemberReq);
            if (!responseVO.isOk()) {
                return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> deleteGroup(DeleteFriendshipGroupReq req) {

        LambdaQueryWrapper<ImFriendshipGroupEntity> query;
        ImFriendshipGroupEntity entity;
        for (String groupName : req.getGroupName()) {
            query = new LambdaQueryWrapper<>();
            query.eq(ImFriendshipGroupEntity::getGroupName, groupName)
                    .eq(ImFriendshipGroupEntity::getAppId, req.getAppId())
                    .eq(ImFriendshipGroupEntity::getFromId, req.getFromId())
                    .eq(ImFriendshipGroupEntity::getDelFlag, DelFlagEnum.DELETE.getCode());

            entity = imFriendshipGroupMapper.selectOne(query);
            if (entity != null) {
                entity.setDelFlag(DelFlagEnum.DELETE.getCode());
                entity.setUpdateTime(System.currentTimeMillis());
                imFriendshipGroupMapper.update(entity, query);
                delAllGroupMember(entity.getGroupId());
            }
        }

        return null;
    }

    @Override
    public ResponseVO<?> getGroup(CheckFriendshipGroupMemberReq req) {
        QueryWrapper<ImFriendshipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", req.getGroupName());
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendshipGroupEntity entity = imFriendshipGroupMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> addGroupMember(AddFriendshipGroupMemberReq req) {
        CheckFriendshipGroupMemberReq groupMemberReq = new CheckFriendshipGroupMemberReq();
        groupMemberReq.setFromId(req.getFromId());
        groupMemberReq.setGroupName(req.getGroupName());
        groupMemberReq.setAppId(req.getAppId());

        ResponseVO<?> group = getGroup(groupMemberReq);
        if (!group.isOk()) {
            return group;
        }
        ImFriendshipGroupEntity entity = (ImFriendshipGroupEntity) group.getData();
        List<String> successId = new ArrayList<>();
        UserId userId;
        for (String toId : req.getToIds()) {
            userId = new UserId();
            userId.setUserId(toId);
            userId.setAppId(req.getAppId());
            userId.setOperator(toId);

            ResponseVO<?> singleUserInfo = imUserService.getSingleUserInfo(userId);
            if (singleUserInfo.isOk()) {
                int i = doAddGroupMember(entity.getGroupId(), toId);
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }

        return ResponseVO.successResponse(successId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> delSpecGroupMember(DeleteFriendshipGroupMemberReq req) {
        CheckFriendshipGroupMemberReq groupMemberReq = new CheckFriendshipGroupMemberReq();
        groupMemberReq.setFromId(req.getFromId());
        groupMemberReq.setGroupName(req.getGroupName());
        groupMemberReq.setAppId(req.getAppId());

        ResponseVO<?> group = getGroup(groupMemberReq);
        if (!group.isOk()) {
            return group;
        }

        ImFriendshipGroupEntity entity = (ImFriendshipGroupEntity) group.getData();
        List<String> successId = new ArrayList<>();
        UserId userId;
        for (String toId : req.getToIds()) {
            userId = new UserId();
            userId.setUserId(toId);
            userId.setAppId(req.getAppId());
            userId.setOperator(toId);
            ResponseVO<?> singleUserInfo = imUserService.getSingleUserInfo(userId);
            if (singleUserInfo.isOk()) {
                int i = deleteGroupMember(entity.getGroupId(), toId);
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }

        return ResponseVO.successResponse(successId);
    }

    @Override
    public ResponseVO<?> delAllGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id", groupId);
        int delete = imFriendShipGroupMemberMapper.delete(query);
        if (delete != 1) {
            return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_SHIP_GROUP_MEMBER_DELETE_ERROR);
        }
        return ResponseVO.successResponse();
    }

    public int deleteGroupMember(Long groupId, String toId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        queryWrapper.eq("to_id", toId);

        return imFriendShipGroupMemberMapper.delete(queryWrapper);
    }

    private int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);

        try {
            return imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
