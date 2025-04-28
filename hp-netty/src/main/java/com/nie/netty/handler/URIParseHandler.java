package com.nie.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

public class URIParseHandler extends ChannelInboundHandlerAdapter {

    private static final String USER_ID_PARAM = "userId";  // 常量化 userId 参数键名

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            String uri = httpRequest.uri();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);

            // 解析 userId 参数
            String userId = null;
            if (queryStringDecoder.parameters().containsKey("userId")) {
                userId = queryStringDecoder.parameters().get("userId").get(0);  // 获取第一个 userId 参数值
            }

            if (userId != null) {
                // 如果 userId 存在，设置到 Channel 的属性中
                Channel channel = ctx.channel();
                channel.attr(AttributeKey.valueOf("userId")).set(userId);
                System.out.println("User ID from URI: " + userId); // 打印确认
            } else {
                System.out.println("User ID not found in URI.");
            }

            // 继续传递消息到下一个处理器
            super.channelRead(ctx, msg);
        } else {
            System.out.println("Not a FullHttpRequest, skipping URI parsing.");
            super.channelRead(ctx, msg);
        }
    }
}
