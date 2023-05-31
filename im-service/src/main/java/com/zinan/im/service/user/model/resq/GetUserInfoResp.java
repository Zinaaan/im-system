package com.zinan.im.service.user.model.resq;

import com.zinan.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @author lzn
 * @date 2023/05/30 18:59
 * @description
 */
@Data
public class GetUserInfoResp {

    private List<ImUserDataEntity> userDataItem;

    private List<String> failUser;
}