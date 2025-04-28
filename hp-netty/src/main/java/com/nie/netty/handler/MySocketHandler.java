package com.nie.netty.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nie.common.tools.RedisOPS;
import com.nie.feign.dto.Sysmessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class MySocketHandler extends SimpleChannelInboundHandler<Sysmessage> {
    //private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static ChannelGroup channelGroup;

    public MySocketHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        final String addr = ctx.channel().remoteAddress().toString();
        final String userId = addr.split(":")[1];
        System.out.println(userId + ":on line");
        //channelGroup.add(ctx.channel());
        System.out.println(ctx.channel());
        System.out.println("channelGroup size:" + channelGroup.size());
        System.out.println(channelGroup);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        final String addr = ctx.channel().remoteAddress().toString();
        final String userId = addr.split(":")[1];
        channelGroup.remove(ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Sysmessage sysmessage) throws Exception {
        final String obj = objectMapper.writeValueAsString(sysmessage);
        final TextWebSocketFrame frame = new TextWebSocketFrame(obj);
        channelGroup.writeAndFlush(frame);
    }


}
