package com.zinan.im.service.user.controller;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.user.model.req.ImportUserReq;
import com.zinan.im.service.user.service.ImUserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzn
 * @date 2023/05/30 19:03
 * @description
 */
@RestController
@RequestMapping("v1/user")
public class ImUserController {

    private final ImUserService imUserService;

    public ImUserController(ImUserService imUserService) {
        this.imUserService = imUserService;
    }

    @RequestMapping("importUser")
    public ResponseVO<?> importUser(@RequestBody ImportUserReq req) {
        return imUserService.importUser(req);
    }
}
