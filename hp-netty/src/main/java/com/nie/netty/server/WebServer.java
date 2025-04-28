package com.nie.netty.server;

import com.nie.common.tools.ChannelGroupManage;
import com.nie.netty.handler.ChatSocketHandler;
import com.nie.netty.handler.MySocketHandler;
import com.nie.netty.handler.URIParseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebServer {
    private int MAX_CONNECT = 10;
    private ChannelGroup channelGroup = ChannelGroupManage.getChannelGroup();

    public WebServer() {
    }

//    public static WebServer initServer() {
//        return new WebServer();
//    }

    public void startServer() throws Exception {
        final EventLoopGroup boss = new NioEventLoopGroup(1);
        final EventLoopGroup worker = new NioEventLoopGroup();
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, MAX_CONNECT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            final ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new HttpServerCodec());  // 编码解码 HTTP 请求
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpObjectAggregator(8192));  // 聚合 HTTP 请求
                            //pipeline.addLast(new URIParseHandler());  // 解析 URI 参数
                            pipeline.addLast(new WebSocketServerProtocolHandler("/netty/hello"));  // WebSocket 握手
                            pipeline.addLast(new ChatSocketHandler());  // 处理 WebSocket 消息

                        }
                    });

            final ChannelFuture channelFuture = serverBootstrap.bind(9091).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.info("Netty 9091  connecting......");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}
