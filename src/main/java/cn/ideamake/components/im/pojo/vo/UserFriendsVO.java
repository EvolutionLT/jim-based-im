package cn.ideamake.components.im.pojo.vo;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import lombok.Data;

import java.util.List;

/**
 * @author Walt
 * @date 2019-09-13 10:45
 * 好友列表中的子单元，一条聊天纪录
 */
@Data
public class UserFriendsVO {
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 最近联系时间
     */
    private long lastMessageTime;
    /**
     * 未读消息数量
     */
    private Integer unReadNum;
    /**
     * 历史消息
     */
    private List<ChatBody> historyMessage;
//    /**
//     * 未读消息,
//     */
//    private List<ChatBody> unReadMessage;
}
