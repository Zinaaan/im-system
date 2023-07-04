package com.zinan.im.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lzn
 * @date 2023/07/04 17:15
 * @description User client entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserClientDto {

    private String userId;
    private Integer appId;
    private Integer clientType;
}
