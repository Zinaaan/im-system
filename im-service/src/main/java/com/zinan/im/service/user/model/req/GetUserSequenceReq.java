package com.zinan.im.service.user.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;

}