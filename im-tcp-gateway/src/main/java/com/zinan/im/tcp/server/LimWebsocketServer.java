package com.zinan.im.tcp.server;

import com.zinan.im.codec.config.BootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lzn
 * @date 2023/06/21 14:29
 * @description
 */
public class LimWebsocketServer {

    private static final Logger log = LoggerFactory.getLogger(LimWebsocketServer.class);

    private final BootstrapConfig.TcpConfig tcpConfig;

    EventLoopGroup bossGroup;

    EventLoopGroup workGroup;

    ServerBootstrap bootstrap;

    public LimWebsocketServer(BootstrapConfig.TcpConfig tcpConfig) {
        this.tcpConfig = tcpConfig;
        bossGroup = new NioEventLoopGroup(tcpConfig.getBossThreadSize());
        workGroup = new NioEventLoopGroup(tcpConfig.getWorkThreadSize());

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
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // Support Http codec since the Websocket protocol is based on Http protocol
                        pipeline.addLast("http-codec", new HttpServerCodec());
                        // Support for chunked data
                        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                        /*
                            Websocket server for handling agreements, specify the router for the client connection -> /ws
                            This handler will do some complicated things like handshaking(close, ping, pong) ping + pong = 心跳

                            Websocket's transmission via <b>frame</b> depends on vary data types
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                    }
                });
    }

    public void start() {
        bootstrap.bind(tcpConfig.getWebsocketPort());
        log.info("Websocket service has been started........");
    }
}
