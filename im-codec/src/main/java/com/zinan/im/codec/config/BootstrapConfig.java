package com.zinan.im.codec.config;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/21 14:56
 * @description
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
    }
}
