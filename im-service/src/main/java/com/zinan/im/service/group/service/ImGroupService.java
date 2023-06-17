package com.zinan.im.service.group.service;

import com.zinan.im.common.ResponseVO;
import com.zinan.im.service.group.model.req.GetGroupReq;
import com.zinan.im.service.group.model.req.ImportGroupReq;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
public interface ImGroupService {

    ResponseVO<?> importGroup(ImportGroupReq req);

    ResponseVO<?> getGroup(GetGroupReq req);
}
