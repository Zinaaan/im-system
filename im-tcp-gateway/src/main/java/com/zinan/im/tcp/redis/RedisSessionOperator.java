package com.zinan.im.tcp.redis;

import com.zinan.im.common.constant.Constants;
import com.zinan.im.common.model.UserClientDto;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * Session operation of Redis
 *
 * @author lzn
 * @date 2023/10/22 15:17
 */
public class RedisSessionOperator {
    private static final RedisSessionOperator INSTANCE = new RedisSessionOperator();
    private static final ThreadLocal<StringBuilder> reusableKey = ThreadLocal.withInitial(StringBuilder::new);

    private RedisSessionOperator() {
    }

    public static RedisSessionOperator getInstance() {
        return INSTANCE;
    }

    private final RedissonClient redissonClient = RedisManager.getRedissonClient();

    private String createRedisMapKey(UserClientDto client) {
        StringBuilder redisMapKey = reusableKey.get();
        redisMapKey.setLength(0);
        return redisMapKey.append(client.getAppId()).append(Constants.RedisConstants.USER_SESSION_CONSTANTS).append(client.getUserId()).toString();
    }

    public <V> void putClientTypeAndSession(UserClientDto client, V session) {
        RMap<Integer, V> map = redissonClient.getMap(createRedisMapKey(client));
        map.put(client.getClientType(), session);
    }

    public <K, V> RMap<K, V> getSessionByClient(UserClientDto client) {
        return redissonClient.getMap(createRedisMapKey(client));
    }

    public void removeSessionByClient(UserClientDto client) {
        getSessionByClient(client).remove(client.getClientType());
    }
}
