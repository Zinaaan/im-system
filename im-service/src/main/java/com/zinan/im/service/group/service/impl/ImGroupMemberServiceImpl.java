package com.zinan.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.enums.DelFlagEnum;
import com.zinan.im.common.enums.GroupErrorCode;
import com.zinan.im.common.enums.GroupMemberRoleEnum;
import com.zinan.im.service.group.dao.ImGroupMemberEntity;
import com.zinan.im.service.group.dao.mapper.ImGroupMemberMapper;
import com.zinan.im.service.group.model.req.AddGroupMemberReq;
import com.zinan.im.service.group.model.req.GetGroupReq;
import com.zinan.im.service.group.model.req.GroupMemberDto;
import com.zinan.im.service.group.model.req.ImportGroupMemberReq;
import com.zinan.im.service.group.model.resp.AddMemberResp;
import com.zinan.im.service.group.service.ImGroupMemberService;
import com.zinan.im.service.group.service.ImGroupService;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import com.zinan.im.service.user.model.req.GetUserInfoReq;
import com.zinan.im.service.user.model.resq.GetUserInfoResp;
import com.zinan.im.service.user.service.ImUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lzn
 * @date 2023/06/10 21:55
 * @description
 */
@Service
public class ImGroupMemberServiceImpl implements ImGroupMemberService {

    private final ImGroupService groupService;

    private final ImUserService userService;

    private final ImGroupMemberMapper groupMemberMapper;

    public ImGroupMemberServiceImpl(ImGroupService groupService, ImUserService userService, ImGroupMemberMapper groupMemberMapper) {
        this.groupService = groupService;
        this.userService = userService;
        this.groupMemberMapper = groupMemberMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> importGroupMember(ImportGroupMemberReq req) {

//        List<AddMemberResp> resp = new ArrayList<>();
        GetGroupReq getReq = new GetGroupReq();
        getReq.setGroupId(req.getGroupId());
        getReq.setAppId(req.getAppId());
        getReq.setOperator(req.getOperator());

        ResponseVO<?> groupResp = groupService.getGroup(getReq);
        if (!groupResp.isOk()) {
            return groupResp;
        }
        AddGroupMemberReq addGroupMemberReq = new AddGroupMemberReq();
        addGroupMemberReq.setGroupId(req.getGroupId());
        addGroupMemberReq.setMembers(req.getMembers());
        addGroupMemberReq.setAppId(req.getAppId());
        addGroupMemberReq.setOperator(req.getOperator());

//        for (GroupMemberDto memberId : req.getMembers()) {
//            ResponseVO<?> responseVO = addGroupMember(req.getGroupId(), req.getAppId(), memberId);
//            AddMemberResp addMemberResp = new AddMemberResp();
//            addMemberResp.setMemberId(memberId.getMemberId());
//            if (responseVO.isOk()) {
//                addMemberResp.setResult(0);
//            } else if (responseVO.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
//                addMemberResp.setResult(2);
//            } else {
//                addMemberResp.setResult(1);
//            }
//            resp.add(addMemberResp);
//        }

//        return ResponseVO.successResponse(resp);
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

        ResponseVO<?> userList = userService.getUserInfo(userInfoReq);
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
                Integer ownerNum = groupMemberMapper.selectCount(queryOwner);
                if (ownerNum > 0) {
                    addMemberResp.setResult(1);
//                    return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_HAVE_OWNER);
                }
            }

            QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
            query.eq("group_id", groupId);
            query.eq("app_id", appId);
            query.eq("member_id", dto.getMemberId());
            ImGroupMemberEntity memberDto = groupMemberMapper.selectOne(query);

            long now = System.currentTimeMillis();
            if (memberDto == null) {
                //初次加群
                memberDto = new ImGroupMemberEntity();
                BeanUtils.copyProperties(dto, memberDto);
                memberDto.setGroupId(groupId);
                memberDto.setAppId(appId);
                memberDto.setJoinTime(now);
                int insert = groupMemberMapper.insert(memberDto);
                if (insert == 1) {
                    addMemberResp.setResult(0);
//                    return ResponseVO.successResponse();
                }
//                return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
                addMemberResp.setResult(2);
            } else if (GroupMemberRoleEnum.LEAVE.getCode() == memberDto.getRole()) {
                //重新进群
                memberDto = new ImGroupMemberEntity();
                BeanUtils.copyProperties(dto, memberDto);
                memberDto.setJoinTime(now);
                int update = groupMemberMapper.update(memberDto, query);
                if (update == 1) {
                    addMemberResp.setResult(0);
                    return ResponseVO.successResponse();
                }
//                return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
                addMemberResp.setResult(2);
            }

            resp.add(addMemberResp);
        }


//        if (dto.getRole() != null && GroupMemberRoleEnum.OWNER.getCode() == dto.getRole()) {
//            QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
//            queryOwner.eq("group_id", groupId);
//            queryOwner.eq("app_id", appId);
//            queryOwner.eq("role", GroupMemberRoleEnum.OWNER.getCode());
//            Integer ownerNum = ii.selectCount(queryOwner);
//            if (ownerNum > 0) {
//                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_HAVE_OWNER);
//            }
//        }
//
//        QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
//        query.eq("group_id", groupId);
//        query.eq("app_id", appId);
//        query.eq("member_id", dto.getMemberId());
//        ImGroupMemberEntity memberDto = imGroupMemberMapper.selectOne(query);
//
//        long now = System.currentTimeMillis();
//        if (memberDto == null) {
//            //初次加群
//            memberDto = new ImGroupMemberEntity();
//            BeanUtils.copyProperties(dto, memberDto);
//            memberDto.setGroupId(groupId);
//            memberDto.setAppId(appId);
//            memberDto.setJoinTime(now);
//            int insert = imGroupMemberMapper.insert(memberDto);
//            if (insert == 1) {
//                return ResponseVO.successResponse();
//            }
//            return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
//        } else if (GroupMemberRoleEnum.LEAVE.getCode() == memberDto.getRole()) {
//            //重新进群
//            memberDto = new ImGroupMemberEntity();
//            BeanUtils.copyProperties(dto, memberDto);
//            memberDto.setJoinTime(now);
//            int update = imGroupMemberMapper.update(memberDto, query);
//            if (update == 1) {
//                return ResponseVO.successResponse();
//            }
//            return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
//        }

//        return ResponseVO.errorResponse(GroupErrorCode.USER_IS_JOINED_GROUP);
        return ResponseVO.successResponse(resp);
    }
}
