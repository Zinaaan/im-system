package com.zinan.im.tcp;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

/**
 * @author lzn
 * @date 2023/07/04 15:25
 * @description
 */
@Slf4j
public class RedissonTest {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        RedissonClient redissonClient = Redisson.create(config);
        // String
//        RBucket<Object> im = redissonClient.getBucket("im");
//        log.info(im.get());
//        im.set("im");
//        log.info(im.get());

        // Hash table
//        RMap<String, String> imMap = redissonClient.getMap("imMap");
//        String client = imMap.get("client");
//        log.info(client);
//        imMap.put("client", "webClient");
//        log.info(imMap.get("client"));

        // Pub-sub
        RTopic topic = redissonClient.getTopic("topic");
        topic.addListener(String.class, (charSequence, s) -> log.info("Message received for client1: " + s));

        RTopic topic2 = redissonClient.getTopic("topic");
        topic2.addListener(String.class, (charSequence, s) -> log.info("Message received for client2: " + s));

        RTopic topic3 = redissonClient.getTopic("topic");
        topic3.publish("hello~client2");
    }
}
