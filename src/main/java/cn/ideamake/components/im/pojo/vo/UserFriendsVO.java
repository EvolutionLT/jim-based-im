package cn.ideamake.components.im.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Walt
 * @date 2019-09-13 10:45
 * 好友列表中的子单元，一条聊天纪录
 */
@Data
public class UserFriendsVO {
    private String userId;
    private String nickname;
    /**
     * 历史消息
     */
    private List<MessageVO> historyMessage;
    /**
     * 未读消息
     */
    private List<MessageVO> unReadMessage;
}
