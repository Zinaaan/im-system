package com.zinan.im.service.group.model.controller;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.group.model.req.ImportGroupReq;
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

    private final ImGroupService groupService;

    public ImGroupController(ImGroupService imGroupService) {
        this.groupService = imGroupService;
    }

    @RequestMapping("/importGroup")
    public ResponseVO<?> importGroup(@RequestBody @Validated ImportGroupReq req)  {
        return groupService.importGroup(req);
    }
}
