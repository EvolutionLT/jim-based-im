package cn.ideamake.components.im.pojo.constant;

public interface VankeRedisKey {
    String VANKE_CHAT_USER_KEY = "vanke:chat:user:%s";

    /**
     * 登录初始化聊天成员信息key
     */
    String VANKE_CHAT_LOGIN_LOCK_KEY = "vanke:chat:login:lock:%s";

    /**
     * 未读消息key
     */
    String VANKE_CHAT_UNREAD_NUM_KEY = "vanke:chat:unread:num:%s:%s";

    /**
     * 所有聊天成员数量key
     */
    String VANKE_CHAT_MEMBER_NUM_KEY = "vanke:chat:all:num:%s";

    /**
     * 所有待回复数量key
     */
    String VANKE_CHAT_PENDING_REPLY_NUM_KEY = "vanke:chat:pending:reply:num:%s";

    /**
     * 最近联系人数量key
     */
    String VANKE_CHAT_LASTED_CONTACT_SNUM_KEY = "vanke:chat:lasted:contanct:num:%s";
}
