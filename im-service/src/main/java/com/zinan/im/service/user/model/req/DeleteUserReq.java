package com.zinan.im.service.user.model.req;

import com.zinan.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author lzn
 * @date 2023/05/30 17:02
 * @description
 */
@Data
public class DeleteUserReq extends RequestBase {

    @NotEmpty(message = "The userId can not be null")
    private List<String> userId;
}