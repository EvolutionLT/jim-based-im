package cn.ideamake.components.im.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program jio-based-im
 * @description: 聊天消息VO
 * @author: apollo
 * @create: 2019/09/26 15:12
 */
@Data
public class ChatInfoVO implements Serializable {
    /**
     * 待回复数量
     */
    private Long pendingReplyNum;

    /**
     * 最近联系人数量
     */
    private Long lastedContactsNum;

    /**
     * 总联系人数量
     */
    private Long allContactsNum;

    /**
     * 未读消息
     */
    private Long unReadNum;

    /**
     * 访客id
     */
    private String visitorId;

    /**
     * 客服id
     */
    private String cusId;


}
