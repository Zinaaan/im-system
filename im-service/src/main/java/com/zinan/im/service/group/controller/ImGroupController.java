package com.zinan.im.service.group.controller;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.group.model.req.*;
import com.zinan.im.service.group.service.ImGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzn
 * @date 2023/06/10 21:50
 * @description
 */
@RestController
@RequestMapping("v1/group")
public class ImGroupController {

    private final ImGroupService imGroupService;

    public ImGroupController(ImGroupService imGroupService) {
        this.imGroupService = imGroupService;
    }

    @RequestMapping("/importGroup")
    public ResponseVO<?> importGroup(@RequestBody @Validated ImportGroupReq req) {
        return imGroupService.importGroup(req);
    }

    @RequestMapping("/updateGroup")
    public ResponseVO<?> update(@RequestBody @Validated UpdateGroupReq req) {
        return imGroupService.updateGroupInfo(req);
    }

    @RequestMapping("/createGroup")
    public ResponseVO<?> createGroup(@RequestBody @Validated CreateGroupReq req) {
        return imGroupService.createGroup(req);
    }

    @RequestMapping("/getGroupInfo")
    public ResponseVO<?> getGroupInfo(@RequestBody @Validated GetGroupReq req) {
        return imGroupService.getGroupInfo(req);
    }

    @RequestMapping("/getJoinedGroup")
    public ResponseVO<?> getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req) {
        return imGroupService.getJoinedGroup(req);
    }

    @RequestMapping("/destroyGroup")
    public ResponseVO<?> destroyGroup(@RequestBody @Validated DestroyGroupReq req) {
        return imGroupService.destroyGroup(req);
    }

    @RequestMapping("/transferGroup")
    public ResponseVO<?> transferGroup(@RequestBody @Validated TransferGroupReq req) {
        return imGroupService.transferGroup(req);
    }

    @RequestMapping("/muteGroup")
    public ResponseVO<?> muteGroup(@RequestBody @Validated MuteGroupReq req)  {
        return imGroupService.muteGroup(req);
    }

    @RequestMapping("/importGroupMember")
    public ResponseVO<?> importGroupMember(@RequestBody @Validated ImportGroupMemberReq req) {
        return imGroupService.importGroupMember(req);
    }

    @RequestMapping("/addGroupMember")
    public ResponseVO<?> addGroupMember(@RequestBody @Validated AddGroupMemberReq req) {
        return imGroupService.addGroupMember(req);
    }

    @RequestMapping("/removeGroupMember")
    public ResponseVO<?> removeGroupMember(@RequestBody @Validated RemoveGroupMemberReq req)  {
        return imGroupService.removeGroupMember(req);
    }

    @RequestMapping("/updateGroupMember")
    public ResponseVO<?> updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req)  {
        return imGroupService.updateGroupMember(req);
    }

    @RequestMapping("/muteGroupMember")
    public ResponseVO<?> muteGroupMember(@RequestBody @Validated MuteMemberReq req)  {
        return imGroupService.muteGroupMember(req);
    }
}
