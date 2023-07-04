package com.zinan.im.common.constant;

/**
 * @author lzn
 * @date 2023/07/04 16:08
 * @description Constant pool for im-system
 */
public class Constants {

    public static class RedisConstants{

        /**
         * User session, format -> appId + USER_SESSION_CONSTANTS + userId
         * e.g. 10000:userSession:lld
         */
        public static final String USER_SESSION_CONSTANTS = ":userSession:";
    }
}
