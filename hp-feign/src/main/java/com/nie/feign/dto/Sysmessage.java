package com.nie.feign.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author author
 * @since 2025-01-19
 */
@TableName("sysmessage")
//redis存入对象的时候通常会带上类的描述消息，为了支持序列化等，使用下面注解可以忽略
//@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知的属性
//@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class Sysmessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "msg_id", type = IdType.AUTO)
    private Integer msgId;

    private Integer senderId;

    private String msgtitle;

    private String msgContent;

    private String sendTime;

    private Integer msgStatus;

    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getMsgtitle() {
        return msgtitle;
    }

    public void setMsgtitle(String msgtitle) {
        this.msgtitle = msgtitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(Integer msgStatus) {
        this.msgStatus = msgStatus;
    }

    @Override
    public String toString() {
        return "Sysmessage{" +
                "msgId=" + msgId +
                ", senderId=" + senderId +
                ", msgtitle=" + msgtitle +
                ", msgContent=" + msgContent +
                ", sendTime=" + sendTime +
                ", target=" + target +
                ", msgStatus=" + msgStatus +
                "}";
    }
}
