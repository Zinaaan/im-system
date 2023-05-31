package com.zinan.im.service.user.controller;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.user.model.req.GetUserInfoReq;
import com.zinan.im.service.user.model.req.ModifyUserInfoReq;
import com.zinan.im.service.user.model.req.UserId;
import com.zinan.im.service.user.service.ImUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzn
 * @date 2023/05/30 19:03
 * @description
 */
@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {

    private static final Logger log = LoggerFactory.getLogger(ImUserDataController.class);

    private final ImUserService imUserService;

    public ImUserDataController(ImUserService imUserService) {
        this.imUserService = imUserService;
    }

    @RequestMapping("/getUserInfo")
    public ResponseVO<?> getUserInfo(@RequestBody @Validated GetUserInfoReq req) {
        return imUserService.getUserInfo(req);
    }

    @RequestMapping("/getSingleUserInfo")
    public ResponseVO<?> getSingleUserInfo(@RequestBody @Validated UserId req) {
        return imUserService.getSingleUserInfo(req);
    }

    @RequestMapping("/modifyUserInfo")
    public ResponseVO<?> modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req) {
        return imUserService.modifyUserInfo(req);
    }
}
