package com.zinan.im.tcp;

import com.zinan.im.codec.config.BootstrapConfig;
import com.zinan.im.tcp.redis.RedisManager;
import com.zinan.im.tcp.server.LimServer;
import com.zinan.im.tcp.server.LimWebsocketServer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author lzn
 * @date 2023/06/21 14:14
 * @description Starter for tcp gateway
 * <p>
 * Extract the common configuration to the config.yml, and analysis it and start the tcp service
 */
public class Starter {

    public static void main(String[] args) {
        start("D:\\workspace_new\\im-system\\im-tcp-gateway\\src\\main\\resources\\config.yml");
    }

    private static void start(String path) {
        Yaml yaml = new Yaml();
        try {
            InputStream inputStream = new FileInputStream(path);
            BootstrapConfig bootstrapConfig = yaml.loadAs(inputStream, BootstrapConfig.class);
            new LimServer(bootstrapConfig.getLim()).start();
            new LimWebsocketServer(bootstrapConfig.getLim()).start();

            // Initialize redis
            RedisManager.init(bootstrapConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
