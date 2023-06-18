package com.zinan.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.enums.GroupErrorCode;
import com.zinan.im.common.enums.GroupMemberRoleEnum;
import com.zinan.im.common.enums.GroupStatusEnum;
import com.zinan.im.common.enums.GroupTypeEnum;
import com.zinan.im.common.exception.ApplicationException;
import com.zinan.im.service.group.dao.ImGroupEntity;
import com.zinan.im.service.group.dao.ImGroupMemberEntity;
import com.zinan.im.service.group.dao.mapper.ImGroupMapper;
import com.zinan.im.service.group.dao.mapper.ImGroupMemberMapper;
import com.zinan.im.service.group.model.req.*;
import com.zinan.im.service.group.model.resp.AddMemberResp;
import com.zinan.im.service.group.model.resp.GetGroupResp;
import com.zinan.im.service.group.model.resp.GetJoinedGroupResp;
import com.zinan.im.service.group.model.resp.GetRoleInGroupResp;
import com.zinan.im.service.group.service.ImGroupService;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import com.zinan.im.service.user.model.req.GetUserInfoReq;
import com.zinan.im.service.user.model.req.UserId;
import com.zinan.im.service.user.model.resq.GetUserInfoResp;
import com.zinan.im.service.user.service.ImUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lzn
 * @date 2023/06/10 21:40
 * @description
 */
@Service
public class ImGroupServiceImpl implements ImGroupService {

    private final ImGroupMapper imGroupMapper;

    private final ImUserService imUserService;

    private final ImGroupMemberMapper imGroupMemberMapper;

    public ImGroupServiceImpl(ImGroupMapper imGroupMapper, ImUserService imUserService, ImGroupMemberMapper imGroupMemberMapper) {
        this.imGroupMapper = imGroupMapper;
        this.imUserService = imUserService;
        this.imGroupMemberMapper = imGroupMemberMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> importGroup(ImportGroupReq req) {

        if (StringUtils.isNotEmpty(req.getGroupId())) {
            LambdaQueryWrapper<ImGroupEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ImGroupEntity::getGroupId, req.getGroupId());
            wrapper.eq(ImGroupEntity::getAppId, req.getAppId());
            if (imGroupMapper.selectCount(wrapper) > 0) {
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_EXIST);
            }

        } else {
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();
        BeanUtils.copyProperties(req, imGroupEntity);
        if (req.getCreateTime() == null) {
            imGroupEntity.setCreateTime(System.currentTimeMillis());
        }

        if (req.getCreateTime() == null) {
            imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        }

        int insert = imGroupMapper.insert(imGroupEntity);
        if (insert != 1) {
            throw new ApplicationException(GroupErrorCode.IMPORT_GROUP_ERROR);
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> updateGroupInfo(UpdateGroupReq req) {
        GetGroupReq groupReq = new GetGroupReq();
        groupReq.setGroupId(req.getGroupId());
        groupReq.setAppId(req.getAppId());
        ResponseVO<?> groupExist = getGroupInfo(groupReq);
        if (!groupExist.isOk()) {
            return groupExist;
        }

        ImGroupEntity imGroupEntity = (ImGroupEntity) groupExist.getData();

        boolean isAdmin = false;
        if (!isAdmin) {
            ResponseVO<?> roleInGroup = getRoleInGroup(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!roleInGroup.isOk()) {
                return roleInGroup;
            }

            GetRoleInGroupResp resp = (GetRoleInGroupResp) roleInGroup.getData();
            int role = resp.getRole();
            boolean isManager = role == GroupMemberRoleEnum.ADMIN.getCode();
            boolean isOwner = role == GroupMemberRoleEnum.OWNER.getCode();

            // Only admin could modify info if the current group is public
            if (GroupTypeEnum.PUBLIC.getCode() == imGroupEntity.getGroupType() && !(isManager || isOwner)) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ADMIN_ROLE);
            }
        } else {
            BeanUtils.copyProperties(req, imGroupEntity);
            LambdaQueryWrapper<ImGroupEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ImGroupEntity::getAppId, req.getAppId());
            wrapper.eq(ImGroupEntity::getGroupId, req.getGroupId());

            int update = imGroupMapper.update(imGroupEntity, wrapper);
            if (update != 1) {
                throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getGroupInfo(GetGroupReq req) {
        LambdaQueryWrapper<ImGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImGroupEntity::getAppId, req.getAppId());
        query.eq(ImGroupEntity::getGroupId, req.getGroupId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(query);
        if (imGroupEntity == null) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        GetGroupResp resp = new GetGroupResp();
        BeanUtils.copyProperties(imGroupEntity, resp);
        ResponseVO<?> groupMember = getGroupMember(req.getGroupId(), req.getAppId());
        if (groupMember.isOk()) {
            List<GroupMemberDto> groupMemberDtoList = (List<GroupMemberDto>) groupMember.getData();
            resp.setMemberList(groupMemberDtoList);
        }

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> createGroup(CreateGroupReq req) {
        boolean isAdmin = false;

        if (!isAdmin) {
            req.setOwnerId(req.getOperator());
        }

        if (StringUtils.isEmpty(req.getGroupId())) {
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            // Check whether the group exists
            LambdaQueryWrapper<ImGroupEntity> query = new LambdaQueryWrapper<>();
            query.eq(ImGroupEntity::getGroupId, req.getGroupId());
            query.eq(ImGroupEntity::getAppId, req.getAppId());
            Integer integer = imGroupMapper.selectCount(query);
            if (integer > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }

        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();
        imGroupEntity.setCreateTime(System.currentTimeMillis());
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        BeanUtils.copyProperties(req, imGroupEntity);
        imGroupMapper.insert(imGroupEntity);

        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setMemberId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRoleEnum.OWNER.getCode());
        groupMemberDto.setJoinTime(System.currentTimeMillis());
        List<GroupMemberDto> memberDtoList = req.getMember();
        memberDtoList.add(groupMemberDto);

        AddGroupMemberReq addGroupMemberReq = new AddGroupMemberReq();
        addGroupMemberReq.setGroupId(req.getGroupId());
        addGroupMemberReq.setMembers(memberDtoList);
        addGroupMemberReq.setAppId(req.getAppId());
        addGroupMemberReq.setOperator(req.getOperator());
        addMemberToGroup(addGroupMemberReq);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getJoinedGroup(GetJoinedGroupReq req) {

        ResponseVO<?> memberJoinedGroup = getMemberJoinedGroup(req);
        if (memberJoinedGroup.isOk()) {
            GetJoinedGroupResp resp = new GetJoinedGroupResp();
            Set<String> groupIds = (Set<String>) memberJoinedGroup.getData();
            if (CollectionUtils.isEmpty(groupIds)) {
                resp.setTotalCount(0);
                resp.setGroupList(new ArrayList<>());
                return ResponseVO.successResponse(resp);
            }

            LambdaQueryWrapper<ImGroupEntity> query = new LambdaQueryWrapper<>();
            query.eq(ImGroupEntity::getAppId, req.getAppId());
            query.in(ImGroupEntity::getGroupId, groupIds);

            if (CollectionUtils.isNotEmpty(req.getGroupType())) {
                query.in(ImGroupEntity::getGroupId, req.getGroupType());
            }

            List<ImGroupEntity> groupList = imGroupMapper.selectList(query);
            resp.setGroupList(groupList);
            if (req.getLimit() == null) {
                resp.setTotalCount(groupList.size());
            } else {
                resp.setTotalCount(imGroupMapper.selectCount(query));
            }
            return ResponseVO.successResponse(resp);
        }

        return memberJoinedGroup;
    }

    @Override
    public ResponseVO<?> destroyGroup(DestroyGroupReq req) {

        boolean isAdmin = false;
        LambdaQueryWrapper<ImGroupEntity> objectQueryWrapper = new LambdaQueryWrapper<>();
        objectQueryWrapper.eq(ImGroupEntity::getGroupId, req.getGroupId());
        objectQueryWrapper.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(objectQueryWrapper);
        if (imGroupEntity == null) {
            throw new ApplicationException(GroupErrorCode.PRIVATE_GROUP_CAN_NOT_DISSOLVE);
        }

        if (imGroupEntity.getStatus() == GroupStatusEnum.DISSOLVE.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DISSOLVE);
        }

        if (!isAdmin) {
            if (imGroupEntity.getGroupType() == GroupTypeEnum.PUBLIC.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }

            if (imGroupEntity.getGroupType() == GroupTypeEnum.PUBLIC.getCode() &&
                    !imGroupEntity.getOwnerId().equals(req.getOperator())) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();

        update.setStatus(GroupStatusEnum.DISSOLVE.getCode());
        int update1 = imGroupMapper.update(update, objectQueryWrapper);
        if (update1 != 1) {
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> transferGroup(TransferGroupReq req) {
        ResponseVO<?> roleInGroupOne = getRoleInGroup(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        GetRoleInGroupResp resp = (GetRoleInGroupResp) roleInGroupOne.getData();
        if (resp.getRole() != GroupMemberRoleEnum.OWNER.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        ResponseVO<?> newOwnerRole = getRoleInGroup(req.getGroupId(), req.getOwnerId(), req.getAppId());
        if (!newOwnerRole.isOk()) {
            return newOwnerRole;
        }

        LambdaQueryWrapper<ImGroupEntity> objectQueryWrapper = new LambdaQueryWrapper<>();
        objectQueryWrapper.eq(ImGroupEntity::getGroupId, req.getGroupId());
        objectQueryWrapper.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(objectQueryWrapper);
        if (imGroupEntity.getStatus() == GroupStatusEnum.DISSOLVE.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DISSOLVE);
        }

        ImGroupEntity updateGroup = new ImGroupEntity();
        updateGroup.setOwnerId(req.getOwnerId());
        LambdaUpdateWrapper<ImGroupEntity> updateGroupWrapper = new LambdaUpdateWrapper<>();
        updateGroupWrapper.eq(ImGroupEntity::getAppId, req.getAppId());
        updateGroupWrapper.eq(ImGroupEntity::getGroupId, req.getGroupId());
        imGroupMapper.update(updateGroup, updateGroupWrapper);
        return transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());
    }

    @Override
    public ResponseVO<?> getMemberJoinedGroup(GetJoinedGroupReq req) {

        if (req.getLimit() != null) {
            Page<ImGroupMemberEntity> objectPage = new Page<>(req.getOffset(), req.getLimit());
            LambdaQueryWrapper<ImGroupMemberEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ImGroupMemberEntity::getAppId, req.getAppId());
            wrapper.eq(ImGroupMemberEntity::getMemberId, req.getMemberId());
            IPage<ImGroupMemberEntity> imGroupMemberEntityPage = imGroupMemberMapper.selectPage(objectPage, wrapper);
            Set<String> groupId = new HashSet<>();
            List<ImGroupMemberEntity> records = imGroupMemberEntityPage.getRecords();
            records.forEach(e -> groupId.add(e.getGroupId()));

            return ResponseVO.successResponse(groupId);
        }

        return ResponseVO.successResponse(imGroupMemberMapper.getJoinedGroupId(req.getAppId(), req.getMemberId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> importGroupMember(ImportGroupMemberReq req) {

        GetGroupReq getReq = new GetGroupReq();
        getReq.setGroupId(req.getGroupId());
        getReq.setAppId(req.getAppId());
        getReq.setOperator(req.getOperator());

        ResponseVO<?> groupResp = getGroupInfo(getReq);
        if (!groupResp.isOk()) {
            return groupResp;
        }
        AddGroupMemberReq addGroupMemberReq = new AddGroupMemberReq();
        addGroupMemberReq.setGroupId(req.getGroupId());
        addGroupMemberReq.setMembers(req.getMembers());
        addGroupMemberReq.setAppId(req.getAppId());
        addGroupMemberReq.setOperator(req.getOperator());

        return addMemberToGroup(addGroupMemberReq);
    }

    public ResponseVO<?> addMemberToGroup(AddGroupMemberReq req) {
        String groupId = req.getGroupId();
        Integer appId = req.getAppId();
        List<GroupMemberDto> groupMemberList = req.getMembers();

        GetUserInfoReq userInfoReq = new GetUserInfoReq();
        userInfoReq.setUserIds(groupMemberList.stream().map(GroupMemberDto::getMemberId).collect(Collectors.toList()));
        userInfoReq.setAppId(req.getAppId());
        userInfoReq.setOperator(req.getOperator());

        ResponseVO<?> userList = imUserService.getUserInfo(userInfoReq);
        GetUserInfoResp userResp = (GetUserInfoResp) userList.getData();
        List<ImUserDataEntity> successList = userResp.getUserDataItem();

        groupMemberList = groupMemberList.stream().filter(groupMemberDto ->
                successList.stream().anyMatch(entity -> entity.getUserId().equals(groupMemberDto.getMemberId()))).collect(Collectors.toList());

        List<AddMemberResp> resp = new ArrayList<>();
        AddMemberResp addMemberResp;
        for (GroupMemberDto dto : groupMemberList) {
            addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(dto.getMemberId());
            if (dto.getRole() != null && GroupMemberRoleEnum.OWNER.getCode() == dto.getRole()) {
                LambdaQueryWrapper<ImGroupMemberEntity> queryOwner = new LambdaQueryWrapper<>();
                queryOwner.eq(ImGroupMemberEntity::getAppId, appId);
                queryOwner.eq(ImGroupMemberEntity::getGroupId, groupId);
                queryOwner.eq(ImGroupMemberEntity::getRole, GroupMemberRoleEnum.OWNER.getCode());
                Integer ownerNum = imGroupMemberMapper.selectCount(queryOwner);
                if (ownerNum > 0) {
                    addMemberResp.setResult(1);
                }
            }

            LambdaQueryWrapper<ImGroupMemberEntity> query = new LambdaQueryWrapper<>();
            query.eq(ImGroupMemberEntity::getAppId, appId);
            query.eq(ImGroupMemberEntity::getGroupId, groupId);
            query.eq(ImGroupMemberEntity::getMemberId, dto.getMemberId());
            ImGroupMemberEntity memberDto = imGroupMemberMapper.selectOne(query);

            long now = System.currentTimeMillis();
            if (memberDto == null) {
                //初次加群
                memberDto = new ImGroupMemberEntity();
                BeanUtils.copyProperties(dto, memberDto);
                memberDto.setGroupId(groupId);
                memberDto.setAppId(appId);
                memberDto.setJoinTime(now);
                int insert = imGroupMemberMapper.insert(memberDto);
                if (insert == 1) {
                    addMemberResp.setResult(0);
                }
                addMemberResp.setResult(2);
            } else if (GroupMemberRoleEnum.LEAVE.getCode() == memberDto.getRole()) {
                //重新进群
                memberDto = new ImGroupMemberEntity();
                BeanUtils.copyProperties(dto, memberDto);
                memberDto.setJoinTime(now);
                int update = imGroupMemberMapper.update(memberDto, query);
                if (update == 1) {
                    addMemberResp.setResult(0);
                    return ResponseVO.successResponse();
                }
                addMemberResp.setResult(2);
            }

            resp.add(addMemberResp);
        }

        return ResponseVO.successResponse(resp);
    }

    public ResponseVO<?> getGroupMember(String groupId, Integer appId) {
        List<GroupMemberDto> groupMember = imGroupMemberMapper.getGroupMember(appId, groupId);
        return ResponseVO.successResponse(groupMember);
    }

    public ResponseVO<?> transferGroupMember(String owner, String groupId, Integer appId) {

        // Update stale owner of group
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        LambdaUpdateWrapper<ImGroupMemberEntity> oldWrapper = new LambdaUpdateWrapper<>();
        oldWrapper.eq(ImGroupMemberEntity::getAppId, appId);
        oldWrapper.eq(ImGroupMemberEntity::getGroupId, groupId);
        oldWrapper.eq(ImGroupMemberEntity::getRole, GroupMemberRoleEnum.OWNER.getCode());
        imGroupMemberMapper.update(imGroupMemberEntity, oldWrapper);

        // Update new owner of group
        ImGroupMemberEntity newOwner = new ImGroupMemberEntity();
        newOwner.setRole(GroupMemberRoleEnum.OWNER.getCode());
        LambdaUpdateWrapper<ImGroupMemberEntity> newWrapper = new LambdaUpdateWrapper<>();
        newWrapper.eq(ImGroupMemberEntity::getAppId, appId);
        newWrapper.eq(ImGroupMemberEntity::getGroupId, groupId);
        newWrapper.eq(ImGroupMemberEntity::getMemberId, owner);
        imGroupMemberMapper.update(newOwner, newWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getRoleInGroup(String groupId, String memberId, Integer appId) {
        GetRoleInGroupResp resp = new GetRoleInGroupResp();

        LambdaQueryWrapper<ImGroupMemberEntity> queryOwner = new LambdaQueryWrapper<>();
        queryOwner.eq(ImGroupMemberEntity::getGroupId, groupId);
        queryOwner.eq(ImGroupMemberEntity::getAppId, appId);
        queryOwner.eq(ImGroupMemberEntity::getMemberId, memberId);

        ImGroupMemberEntity imGroupMemberEntity = imGroupMemberMapper.selectOne(queryOwner);
        if (imGroupMemberEntity == null || imGroupMemberEntity.getRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        resp.setSpeakDate(imGroupMemberEntity.getSpeakDate());
        resp.setGroupMemberId(imGroupMemberEntity.getGroupMemberId());
        resp.setMemberId(imGroupMemberEntity.getMemberId());
        resp.setRole(imGroupMemberEntity.getRole());

        return ResponseVO.successResponse(resp);
    }

    /**
     * Group type:
     * <p>
     * 1 -> private, add new group member without group owner approval if the current user is already in this group.
     * 2 -> public, the group owner(creator) can assign a group admin after creation, and group owner or admin approval is required to join the group.
     *
     * @param req: AddGroupMemberReq
     * @return ResponseVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> addGroupMember(AddGroupMemberReq req) {

        boolean isAdmin = false;
        GetGroupReq getGroupReq = new GetGroupReq();
        getGroupReq.setGroupId(req.getGroupId());
        getGroupReq.setAppId(req.getAppId());
        getGroupReq.setOperator(req.getOperator());

        ResponseVO<?> groupResp = getGroupInfo(getGroupReq);
        if (!groupResp.isOk()) {
            return groupResp;
        }

        ImGroupEntity group = (ImGroupEntity) groupResp.getData();
        if (!isAdmin && GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APP_ADMIN_ROLE);
        }

        return addMemberToGroup(req);
    }

    @Override
    public ResponseVO<?> removeGroupMember(RemoveGroupMemberReq req) {
        String groupId = req.getGroupId();
        Integer appId = req.getAppId();
        String operator = req.getOperator();
        boolean isAdmin = false;

        GetGroupReq getGroupReq = new GetGroupReq();
        getGroupReq.setGroupId(groupId);
        getGroupReq.setAppId(appId);
        getGroupReq.setOperator(operator);
        ResponseVO<?> groupResp = getGroupInfo(getGroupReq);
        if (!groupResp.isOk()) {
            return groupResp;
        }

        ImGroupEntity group = (ImGroupEntity) groupResp.getData();

        if (!isAdmin) {
            if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {

                // Got the operator access, admin/owner/member
                ResponseVO<?> role = getRoleInGroup(groupId, operator, appId);
                if (!role.isOk()) {
                    return role;
                }

                GetRoleInGroupResp data = (GetRoleInGroupResp) role.getData();
                Integer roleInfo = data.getRole();

                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                boolean isManager = roleInfo == GroupMemberRoleEnum.ADMIN.getCode();

                if (!isOwner && !isManager) {
                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_ADMIN_ROLE);
                }

                // Only group owner access to remove group members if the group type is private
                if (!isOwner && GroupTypeEnum.PRIVATE.getCode() == group.getGroupType()) {
                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }

                // Both group owner and admin access to remove group member, but admin role only access to remove general group members
                if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
                    //获取被踢人的权限
                    // Got access of group member who would be removed
                    ResponseVO<?> roleInGroupOne = getRoleInGroup(groupId, req.getMemberId(), appId);
                    if (!roleInGroupOne.isOk()) {
                        return roleInGroupOne;
                    }
                    GetRoleInGroupResp memberRole = (GetRoleInGroupResp) roleInGroupOne.getData();
                    if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                        throw new ApplicationException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
                    }
                    // If current user is admin and the user who will be removed is not group member, denied.
                    if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                        throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                    }
                }
            }
        }

        return removeMemberFromGroup(req.getGroupId(), req.getAppId(), req.getMemberId());
    }

    @Override
    public ResponseVO<?> updateGroupMember(UpdateGroupMemberReq req) {
        boolean isAdmin = false;
        GetGroupReq getGroupReq = new GetGroupReq();
        BeanUtils.copyProperties(req, getGroupReq);
        ResponseVO<?> group = getGroupInfo(getGroupReq);
        if (!group.isOk()) {
            return group;
        }

        ImGroupEntity groupData = (ImGroupEntity) group.getData();
        if (groupData.getStatus() == GroupStatusEnum.DISSOLVE.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DISSOLVE);
        }

        //是否是自己修改自己的资料
        boolean isMeOperate = req.getOperator().equals(req.getMemberId());

        if (!isAdmin) {
            // The nickname can only be modified by yourself, and the privileges can only be modified by the group owner or administrator.
            if (StringUtils.isBlank(req.getAlias()) && !isMeOperate) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
            }

            // Private groups cannot have administrators
            if (groupData.getGroupType() == GroupTypeEnum.PRIVATE.getCode() &&
                    req.getRole() != null && (req.getRole() == GroupMemberRoleEnum.ADMIN.getCode() ||
                    req.getRole() == GroupMemberRoleEnum.OWNER.getCode())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ADMIN_ROLE);
            }

            // If we want to modify the privileges related then follow the logic below
            if(req.getRole() != null){
                // Whether the operator's is in the group
                ResponseVO<?> roleInGroupOne = getRoleInGroup(req.getGroupId(), req.getMemberId(), req.getAppId());
                if(!roleInGroupOne.isOk()){
                    return roleInGroupOne;
                }

                // Get operator privileges
                ResponseVO<?> operateRoleInGroupOne = getRoleInGroup(req.getGroupId(), req.getOperator(), req.getAppId());
                if(!operateRoleInGroupOne.isOk()){
                    return operateRoleInGroupOne;
                }

                GetRoleInGroupResp data = (GetRoleInGroupResp) operateRoleInGroupOne.getData();
                Integer roleInfo = data.getRole();
                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                boolean isManager = roleInfo == GroupMemberRoleEnum.ADMIN.getCode();

                // Only admin can modify privileges
                if(req.getRole() != null && !isOwner && !isManager){
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ADMIN_ROLE);
                }

                // Only group owner can set up the admin role
                if(req.getRole() != null && req.getRole() == GroupMemberRoleEnum.ADMIN.getCode() && !isOwner){
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }
            }
        }

        ImGroupMemberEntity update = new ImGroupMemberEntity();
        if (StringUtils.isNotBlank(req.getAlias())) {
            update.setAlias(req.getAlias());
        }

        //不能直接修改为群主
        // Can not be modified directly to the group owner
        if(req.getRole() != null && req.getRole() != GroupMemberRoleEnum.OWNER.getCode()){
            update.setRole(req.getRole());
        }

        UpdateWrapper<ImGroupMemberEntity> objectUpdateWrapper = new UpdateWrapper<>();
        objectUpdateWrapper.eq("app_id", req.getAppId());
        objectUpdateWrapper.eq("member_id", req.getMemberId());
        objectUpdateWrapper.eq("group_id", req.getGroupId());
        imGroupMemberMapper.update(update, objectUpdateWrapper);

        return ResponseVO.successResponse();
    }

    public ResponseVO<?> removeMemberFromGroup(String groupId, Integer appId, String memberId) {

        UserId userId = new UserId();
        userId.setUserId(memberId);
        userId.setAppId(appId);
        userId.setOperator(memberId);

        ResponseVO<?> singleUserInfo = imUserService.getSingleUserInfo(userId);
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        ResponseVO<?> roleInGroupOne = getRoleInGroup(groupId, memberId, appId);
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        GetRoleInGroupResp data = (GetRoleInGroupResp) roleInGroupOne.getData();
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setLeaveTime(System.currentTimeMillis());
        imGroupMemberEntity.setGroupMemberId(data.getGroupMemberId());
        imGroupMemberMapper.updateById(imGroupMemberEntity);

        return ResponseVO.successResponse();
    }

}
