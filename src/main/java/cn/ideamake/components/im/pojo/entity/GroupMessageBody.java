package cn.ideamake.components.im.pojo.entity;

import lombok.Data;

@Data
public class GroupMessageBody {

    /**
     * 消息唯一id
     */
    private String msgId;

    /**
     * 群id
     */
    private Integer userGroupId;

    /**
     * 消息创建时间
     */
    private String createAt;

    /**
     * 消息发送者
     */
    private String sourceUserId;

}
