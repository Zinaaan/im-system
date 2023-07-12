package com.zinan.im.codec.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lzn
 * @date 2023/06/21 14:56
 * @description Configuration mapping for config.xml in im-tcp-gateway/resources
 */
@Data
public class BootstrapConfig {

    private TcpConfig lim;

    @Data
    public static class TcpConfig {

        private Integer tcpPort;
        private Integer websocketPort;
        private Integer bossThreadSize;
        private Integer workThreadSize;
        private Long heartBeatTime;
        /**
         * Redis configurations
         */
        private RedisConfig redis;
        private Rabbitmq rabbitmq;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisConfig {

        /**
         * Redis mode -> single/sentinel/cluster
         * 单机模式：single 哨兵模式：sentinel 集群模式：cluster
         */
        private String mode;
        /**
         * Database name
         */
        private Integer database;
        /**
         * Password
         */
        private String password;

        private Integer timeout;
        /**
         * Minimum idle number
         */
        private Integer poolMinIdle;
        /**
         * Connection timeout time(milliseconds)
         */
        private Integer poolConnTimeout;
        /**
         * Size of the connection pool
         */
        private Integer poolSize;

        private RedisSingle single;
    }

    /**
     * Redis configurations for single mode
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSingle {
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rabbitmq {
        private String host;

        private Integer port;

        private String virtualHost;

        private String userName;

        private String password;
    }
}
