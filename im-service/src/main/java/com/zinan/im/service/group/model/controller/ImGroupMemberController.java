package com.zinan.im.service.group.model.controller;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.group.model.req.ImportGroupMemberReq;
import com.zinan.im.service.group.service.ImGroupMemberService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzn
 * @date 2023/06/17 17:35
 * @description
 */
@RestController
@RequestMapping("v1/group/member")
public class ImGroupMemberController {

    public final ImGroupMemberService imGroupMemberService;

    public ImGroupMemberController(ImGroupMemberService imGroupMemberService) {
        this.imGroupMemberService = imGroupMemberService;
    }

    @RequestMapping("/importGroupMember")
    public ResponseVO<?> importGroupMember(@RequestBody @Validated ImportGroupMemberReq req) {
        return imGroupMemberService.importGroupMember(req);
    }
}
