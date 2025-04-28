package com.nie.common.tools;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ChannelGroupManage {
    public static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void addChannel(Channel channel) {
        channelGroup.add(channel);
    }


    public static void removeChannel(Channel channel) {
        channelGroup.remove(channel);
    }

    public static void broadcastMessage(TextWebSocketFrame frame) {
        channelGroup.writeAndFlush(frame);
    }

    public static ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public static int getChannelCount() {
        return channelGroup.size();
    }
}
