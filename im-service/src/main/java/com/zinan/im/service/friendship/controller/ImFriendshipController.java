package com.zinan.im.service.friendship.controller;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.friendship.model.req.*;
import com.zinan.im.service.friendship.service.ImFriendshipService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzn
 * @date 2023/05/31 19:03
 * @description
 */
@RestController
@RequestMapping("v1/friendship")
public class ImFriendshipController {

    private final ImFriendshipService imFriendshipService;

    public ImFriendshipController(ImFriendshipService imFriendshipService) {
        this.imFriendshipService = imFriendshipService;
    }

    @RequestMapping("/importFriendship")
    public ResponseVO<?> importFriendship(@RequestBody @Validated ImportFriendshipReq req) {
        return imFriendshipService.importFriendship(req);
    }

    @RequestMapping("/addFriend")
    public ResponseVO<?> addFriend(@RequestBody @Validated AddFriendReq req) {
        return imFriendshipService.addFriend(req);
    }

    @RequestMapping("/updateFriend")
    public ResponseVO<?> updateFriend(@RequestBody @Validated UpdateFriendReq req) {
        return imFriendshipService.updateFriend(req);
    }

    @RequestMapping("/deleteFriend")
    public ResponseVO<?> deleteFriend(@RequestBody @Validated DeleteFriendReq req) {
        return imFriendshipService.deleteFriend(req);
    }

    @RequestMapping("/deleteAllFriend")
    public ResponseVO<?> deleteAllFriend(@RequestBody @Validated DeleteFriendReq req) {
        return imFriendshipService.deleteAllFriend(req);
    }

    @RequestMapping("/getAllFriendship")
    public ResponseVO<?> getAllFriendship(@RequestBody @Validated GetAllFriendshipReq req) {
        return imFriendshipService.getAllFriendship(req);
    }

    @RequestMapping("/getFriendship")
    public ResponseVO<?> getFriendship(@RequestBody @Validated GetRelationReq req) {
        return imFriendshipService.getFriendship(req);
    }

    @RequestMapping("/checkFriend")
    public ResponseVO<?> checkFriendship(@RequestBody @Validated CheckFriendshipReq req) {
        return imFriendshipService.checkFriendship(req);
    }

    @RequestMapping("/addToBlackList")
    public ResponseVO<?> addToBlackList(@RequestBody @Validated AddFriendshipBlackReq req) {
        return imFriendshipService.addToBlackList(req);
    }

    @RequestMapping("/deleteFromBlackList")
    public ResponseVO<?> deleteFromBlackList(@RequestBody @Validated DeleteBlackReq req) {
        return imFriendshipService.deleteFromBlackList(req);
    }

    @RequestMapping("/checkIfInBlackList")
    public ResponseVO<?> checkIfInBlackList(@RequestBody @Validated CheckFriendshipReq req) {
        return imFriendshipService.checkIfInBlackList(req);
    }

    @RequestMapping("/approveFriendRequest")
    public ResponseVO<?> approveFriendRequest(@RequestBody @Validated ApproverFriendRequestReq req) {
        return imFriendshipService.approveFriendRequest(req);
    }

    @RequestMapping("/getFriendshipRequest")
    public ResponseVO<?> getFriendshipRequest(@RequestBody @Validated GetFriendshipRequestReq req) {
        return imFriendshipService.getFriendshipRequest(req);
    }

    @RequestMapping("/readFriendshipRequest")
    public ResponseVO<?> readFriendshipRequest(@RequestBody @Validated ReadFriendshipRequestReq req) {
        return imFriendshipService.readFriendshipRequest(req);
    }

//    @RequestMapping("/syncFriendshipList")
//    public ResponseVO<?> syncFriendshipList(@RequestBody @Validated SyncReq req, Integer appId){
//        req.setAppId(appId);
//        return imFriendShipService.syncFriendshipList(req);
//    }
}
