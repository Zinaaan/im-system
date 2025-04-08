package com.zinan.im.codec.pack;

import lombok.Data;

/**
 * Gateway log in verification via only user id
 *
 * @author lzn
 * @date 2023/07/04 14:12
 */
@Data
public class LoginPack {

    private String userId;
}
