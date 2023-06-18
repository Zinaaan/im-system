package com.zinan.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        addGroupMember(addGroupMemberReq);

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

        return addGroupMember(addGroupMemberReq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> addGroupMember(AddGroupMemberReq req) {
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
                QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
                queryOwner.eq("group_id", groupId);
                queryOwner.eq("app_id", appId);
                queryOwner.eq("role", GroupMemberRoleEnum.OWNER.getCode());
                Integer ownerNum = imGroupMemberMapper.selectCount(queryOwner);
                if (ownerNum > 0) {
                    addMemberResp.setResult(1);
                }
            }

            QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
            query.eq("group_id", groupId);
            query.eq("app_id", appId);
            query.eq("member_id", dto.getMemberId());
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

}
