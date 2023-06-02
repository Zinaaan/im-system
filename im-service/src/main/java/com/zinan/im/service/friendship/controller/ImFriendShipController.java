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
public class ImFriendShipController {

    private final ImFriendshipService imFriendShipService;

    public ImFriendShipController(ImFriendshipService imFriendShipService) {
        this.imFriendShipService = imFriendShipService;
    }

    @RequestMapping("/importFriendShip")
    public ResponseVO<?> importFriendShip(@RequestBody @Validated ImportFriendShipReq req) {
        return imFriendShipService.importFriendShip(req);
    }

    @RequestMapping("/addFriend")
    public ResponseVO<?> addFriend(@RequestBody @Validated AddFriendReq req) {
        return imFriendShipService.addFriend(req);
    }

    @RequestMapping("/updateFriend")
    public ResponseVO<?> updateFriend(@RequestBody @Validated UpdateFriendReq req) {
        return imFriendShipService.updateFriend(req);
    }

    @RequestMapping("/deleteFriend")
    public ResponseVO<?> deleteFriend(@RequestBody @Validated DeleteFriendReq req) {
        return imFriendShipService.deleteFriend(req);
    }

    @RequestMapping("/deleteAllFriend")
    public ResponseVO<?> deleteAllFriend(@RequestBody @Validated DeleteFriendReq req) {
        return imFriendShipService.deleteAllFriend(req);
    }

    @RequestMapping("/getAllFriendship")
    public ResponseVO<?> getAllFriendship(@RequestBody @Validated GetAllFriendShipReq req){
        return imFriendShipService.getAllFriendship(req);
    }

    @RequestMapping("/getFriendship")
    public ResponseVO<?> getFriendship(@RequestBody @Validated GetRelationReq req){
        return imFriendShipService.getFriendship(req);
    }

    @RequestMapping("/checkFriend")
    public ResponseVO<?> checkFriendship(@RequestBody @Validated CheckFriendShipReq req){
        return imFriendShipService.checkFriendship(req);
    }

    @RequestMapping("/addToBlackList")
    public ResponseVO<?> addToBlackList(@RequestBody @Validated AddFriendShipBlackReq req){
        return imFriendShipService.addToBlackList(req);
    }

    @RequestMapping("/deleteFromBlackList")
    public ResponseVO<?> deleteFromBlackList(@RequestBody @Validated DeleteBlackReq req){
        return imFriendShipService.deleteFromBlackList(req);
    }

    @RequestMapping("/checkIfInBlackList")
    public ResponseVO<?> checkIfInBlackList(@RequestBody @Validated CheckFriendShipReq req){
        return imFriendShipService.checkIfInBlackList(req);
    }

//    @RequestMapping("/syncFriendshipList")
//    public ResponseVO<?> syncFriendshipList(@RequestBody @Validated SyncReq req, Integer appId){
//        req.setAppId(appId);
//        return imFriendShipService.syncFriendshipList(req);
//    }
}
