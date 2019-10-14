package cn.ideamake.components.im.pojo.entity;

import cn.ideamake.components.im.common.common.packets.Message;
import lombok.Data;

/**
 * @author evolution
 * @title: IMChatBody
 * @projectName im
 * @description: TODO  消息实体类
 * @date 2019-07-07 10:01
 * @ltd：思为
 */
@Data
public class IMChatBody extends Message {

    private static final long serialVersionUID = 5731474214655476286L;
    /**
     * 发送用户id;
     */
    private String from;
    /**
     * 目标用户id;
     */
    private String to;
    /**
     * 消息类型;(如：0:text、1:image、2:voice、3:vedio、4:music、5:news)
     */
    private Integer msgType;
    /**
     * 聊天类型;(如公聊、私聊)
     */
    private Integer chatType;
    /**
     * 消息内容;
     */
    private String content;
    /**
     * 消息发到哪个群组;
     */
    private String group_id;

    private IMChatBody(){}
}
