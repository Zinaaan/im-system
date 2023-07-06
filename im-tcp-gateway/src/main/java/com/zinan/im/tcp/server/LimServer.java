package com.zinan.im.tcp.server;

import com.zinan.im.codec.MessageDecoder;
import com.zinan.im.codec.config.BootstrapConfig;
import com.zinan.im.tcp.handler.HeartBeatHandler;
import com.zinan.im.tcp.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lzn
 * @date 2023/06/21 14:15
 * @description
 */
public class LimServer {

    private static final Logger log = LoggerFactory.getLogger(LimServer.class);

    private final BootstrapConfig.TcpConfig tcpConfig;

    private final ServerBootstrap bootstrap;

    public LimServer(BootstrapConfig.TcpConfig tcpConfig) {
        this.tcpConfig = tcpConfig;
        EventLoopGroup bossGroup = new NioEventLoopGroup(tcpConfig.getBossThreadSize());
        EventLoopGroup workGroup = new NioEventLoopGroup(tcpConfig.getWorkThreadSize());

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                // The available queue size
                .option(ChannelOption.SO_BACKLOG, 10240)
                // true -> available for reuse local address and port
                .option(ChannelOption.SO_REUSEADDR, true)
                // true -> forbidden Nagle algorithm. Affects the real-time nature of messages if false
                .option(ChannelOption.TCP_NODELAY, true)
                // true -> The server will send heartbeats packages if there is no data for 2 hours
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new MessageDecoder());
                        /*
                         * If read and write timeout events(IdleState.ALL_IDLE) is triggered,
                         * the socket channel will invoke the #{userEventTriggered} event of the next handler(currently is HeartBeatHandler)
                         */
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 10));
                        socketChannel.pipeline().addLast(new HeartBeatHandler(tcpConfig.getHeartBeatTime()));
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                });
    }

    public void start() {
        bootstrap.bind(tcpConfig.getTcpPort());
        log.info("Tcp service has been started........");
    }
}
