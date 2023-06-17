package com.zinan.im.service.group.service;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.group.model.req.AddGroupMemberReq;
import com.zinan.im.service.group.model.req.ImportGroupMemberReq;

/**
 * @author lzn
 * @date 2023/06/10 21:55
 * @description
 */
public interface ImGroupMemberService {

    ResponseVO<?> importGroupMember(ImportGroupMemberReq req);

    ResponseVO<?> addGroupMember(AddGroupMemberReq req);
}
