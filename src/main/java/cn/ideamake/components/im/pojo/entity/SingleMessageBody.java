package cn.ideamake.components.im.pojo.entity;

import lombok.Data;

@Data
public class SingleMessageBody {

    /**
     * 消息唯一id
     */
    private String msgId;

    /**
     * 消息发送者
     */
    private String sourceUserId;

    /**
     * 消息接收者
     */
    private String targetUserId;

    /**
     * 消息创建时间
     */
    private String createAt;

    /**
     * 消息状态0-未读，1-已读，备用字段，后续扩展使用
     */
    private Integer msgStatus;

    /**
     * 文本消息内容
     */
    private String msgContent;

    /**
     * 消息类型
     */
    private String msgType;

}
