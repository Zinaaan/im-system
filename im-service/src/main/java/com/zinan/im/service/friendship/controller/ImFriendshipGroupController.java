package com.zinan.im.service.friendship.controller;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.zinan.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.zinan.im.service.friendship.model.req.DeleteFriendshipGroupMemberReq;
import com.zinan.im.service.friendship.model.req.DeleteFriendshipGroupReq;
import com.zinan.im.service.friendship.service.ImFriendshipGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @author lzn
 * @date 2023/06/04 19:03
 * @description
 */
@RestController
@RequestMapping("v1/friendship/group")
public class ImFriendshipGroupController {

    private final ImFriendshipGroupService imFriendShipGroupService;

    public ImFriendshipGroupController(ImFriendshipGroupService imFriendshipGroupService) {
        this.imFriendShipGroupService = imFriendshipGroupService;
    }

    @RequestMapping("/add")
    public ResponseVO<?> add(@RequestBody @Validated AddFriendshipGroupReq req) {
        return imFriendShipGroupService.addGroup(req);
    }

    @RequestMapping("/del")
    public ResponseVO<?> del(@RequestBody @Validated DeleteFriendshipGroupReq req) {
        return imFriendShipGroupService.deleteGroup(req);
    }

    @RequestMapping("/member/add")
    public ResponseVO<?> memberAdd(@RequestBody @Validated AddFriendshipGroupMemberReq req) {
        return imFriendShipGroupService.addGroupMember(req);
    }

    @RequestMapping("/member/delSpecGroupMember")
    public ResponseVO<?> delSpecGroupMember(@RequestBody @Validated DeleteFriendshipGroupMemberReq req) {
        return imFriendShipGroupService.delSpecGroupMember(req);
    }

    @RequestMapping("/member/delAllGroupMember")
    public ResponseVO<?> delAllGroupMember(@RequestBody @NotNull Long groupId) {
        return imFriendShipGroupService.delAllGroupMember(groupId);
    }
}
