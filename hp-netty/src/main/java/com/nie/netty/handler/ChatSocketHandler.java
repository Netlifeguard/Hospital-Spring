package com.nie.netty.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nie.common.tools.MQConstant;
import com.nie.common.tools.MqProducer;
import com.nie.feign.dto.MessageDTO;
import com.nie.netty.config.ESConfig;
import com.nie.netty.config.ObjectMapperConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeanUtils;

import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class ChatSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static ObjectMapper objectMapper = ObjectMapperConfig.createObjectMapper();
    //private static Map<String, Channel> single = new HashMap<>();
    private ESConfig esConfig = new ESConfig();
    private static final RestHighLevelClient client = ESConfig.getClient();

    private boolean isFirst = false;
    private String userId = "";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        System.out.println(channels.size());
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println(userId + ": off line");
        this.isFirst = false;
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        if (!isFirst) {
            userId = textWebSocketFrame.text();
            log.info("userId online... {}", userId);
            isFirst = true;
            esConfig.initES(userId);
        } else {

            System.out.println("receive the msg:" + userId + textWebSocketFrame.text());
            final String text = textWebSocketFrame.text();
            final MessageDTO msg = new MessageDTO();
            msg.setUserId(userId);
            msg.setContent(text);
            final String result = objectMapper.writeValueAsString(msg);
            channels.writeAndFlush(new TextWebSocketFrame(result), channel -> channel != ctx.channel());

            //save to es
            final MessageDTO msgRecord = new MessageDTO();
            final String randomId = genRandomId(5);
            log.info("doc_id  {}", randomId);
            final LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            BeanUtils.copyProperties(msg, msgRecord);
            msgRecord.setMsgId(randomId);
            msgRecord.setSendTime(formatter.format(date));

            final String resRecord = objectMapper.writeValueAsString(msgRecord);
            if (client != null) {
                log.info("es client is not null");
            }
            esConfig.createDoc(client, userId, resRecord, randomId);
        }


    }

    public String genRandomId(int len) {
        final ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final int rnum = localRandom.nextInt(10);
            sb.append(rnum);
        }
        return sb.toString();
    }
}
