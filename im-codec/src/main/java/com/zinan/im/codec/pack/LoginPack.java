package com.zinan.im.codec.pack;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/07/04 14:12
 * @description Gateway log in verification via only user id
 */
@Data
public class LoginPack {

    private String userId;
}
