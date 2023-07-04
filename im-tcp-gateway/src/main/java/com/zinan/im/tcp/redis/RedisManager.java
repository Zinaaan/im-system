package com.zinan.im.tcp.redis;

import com.zinan.im.codec.config.BootstrapConfig;
import org.redisson.api.RedissonClient;

/**
 * @author lzn
 * @date 2023/07/04 16:00
 * @description
 */
public class RedisManager {

    private static RedissonClient redissonClient;

    public static void init(BootstrapConfig config) {
        SingleClientUtils singleClientUtils = new SingleClientUtils();
        redissonClient = singleClientUtils.getRedissonClient(config.getLim().getRedis());
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
