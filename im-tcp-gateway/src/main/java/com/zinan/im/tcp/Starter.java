package com.zinan.im.tcp;

import com.zinan.im.codec.config.BootstrapConfig;
import com.zinan.im.tcp.server.LimServer;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * @author lzn
 * @date 2023/06/21 14:14
 * Starter for tcp gateway
 * Extract the common configuration to the config.yml, and analysis it and start the tcp service
 */
public class Starter {

    public static void main(String[] args) {
        start("config.yml");
    }

    private static void start(String path) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Starter.class.getClassLoader().getResourceAsStream(path)){
            BootstrapConfig bootstrapConfig = yaml.loadAs(inputStream, BootstrapConfig.class);
            new LimServer(bootstrapConfig.getLim()).start();
//            new LimWebsocketServer(bootstrapConfig.getLim()).start();

            // Initialize redis
//            RedisManager.init(bootstrapConfig);
//            // Initialize Rabbit Mq
//            MqFactory.init(bootstrapConfig.getLim().getRabbitmq());
//            MessageReceiver.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
