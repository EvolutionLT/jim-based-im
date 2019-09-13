package cn.ideamake.components.im.pojo.vo;

import lombok.Data;

/**
 * @author Walt
 * @date 2019-09-13 10:54
 */
@Data
public class MessageVO {
    /**
     * 聊天消息类型
     */
    private String chatType;

    /**
     * 指令
     */
    private Integer cmd;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送人
     */
    private String from;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 接收者
     */
    private String to;
    /**
     * 消息id
     */
    private String id;
}
