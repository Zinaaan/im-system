package com.zinan.im.common.model;

import lombok.Data;
import javax.validation.constraints.NotEmpty;


/**
 * @author lzn
 * @date 2023/05/30 18:26
 * @description
 */
@Data
public class RequestBase {

    @NotEmpty(message = "appId can not be null")
    private Integer appId;

    private String operator;
}
