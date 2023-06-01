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

    @RequestMapping("/getAllFriendRelationship")
    public ResponseVO<?> getAllFriendShip(@RequestBody @Validated GetAllFriendShipReq req){
        return imFriendShipService.getAllFriendRelationship(req);
    }

    @RequestMapping("/getFriendRelationship")
    public ResponseVO<?> getRelation(@RequestBody @Validated GetRelationReq req){
        return imFriendShipService.getFriendRelationship(req);
    }
//
//    @RequestMapping("/checkFriend")
//    public ResponseVO<?> checkFriend(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
//        req.setAppId(appId);
//        return imFriendShipService.checkFriendship(req);
//    }
//
//    @RequestMapping("/addBlack")
//    public ResponseVO<?> addBlack(@RequestBody @Validated AddFriendShipBlackReq req, Integer appId){
//        req.setAppId(appId);
//        return imFriendShipService.addBlack(req);
//    }
//
//    @RequestMapping("/deleteBlack")
//    public ResponseVO<?> deleteBlack(@RequestBody @Validated DeleteBlackReq req, Integer appId){
//        req.setAppId(appId);
//        return imFriendShipService.deleteBlack(req);
//    }
//
//    @RequestMapping("/checkBlck")
//    public ResponseVO<?> checkBlck(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
//        req.setAppId(appId);
//        return imFriendShipService.checkBlck(req);
//    }
//
//
//    @RequestMapping("/syncFriendshipList")
//    public ResponseVO<?> syncFriendshipList(@RequestBody @Validated SyncReq req, Integer appId){
//        req.setAppId(appId);
//        return imFriendShipService.syncFriendshipList(req);
//    }
}
