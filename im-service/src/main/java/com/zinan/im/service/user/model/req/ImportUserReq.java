package com.zinan.im.service.user.model.req;

import com.zinan.im.common.model.RequestBase;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userData;
}
