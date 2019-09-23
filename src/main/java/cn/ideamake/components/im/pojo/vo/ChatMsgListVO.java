package cn.ideamake.components.im.pojo.vo;

import lombok.Data;

/**
 * @author evolution
 * @title: ChatMsgList
 * @projectName im
 * @description: TODO
 * @date 2019-07-08 11:35
 * @ltd：思为
 */
@Data
public class ChatMsgListVO {
    /**
     * id
     */
   private String id;
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 消息
     */
    private String msgContent;
    /**
     * 消息json字符串
     */
    private String jsonContent;

    /**
     * 消息时间
     */
    private String dates;
    /**
     * 房间ID
     */
    private String roomId;

 /**
  * 消息主键
  */
 private String  msgId;

 /**
  * 是否已读
  */
 private String isRead;

 /**
  * 是否为在线消息
  */
 private String isOnline;



}
