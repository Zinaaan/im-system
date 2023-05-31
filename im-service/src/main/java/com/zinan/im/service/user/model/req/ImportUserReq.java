package com.zinan.im.service.user.model.req;

import com.zinan.im.common.model.RequestBase;
import com.zinan.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
public class ImportUserReq extends RequestBase {

    @NotNull(message = "The userData can not be null")
    @Size(min = 1, message = "At least one user is required")
    private List<ImUserDataEntity> userData;
}
