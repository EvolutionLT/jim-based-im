package cn.ideamake.components.im.service.vanke;

import cn.ideamake.components.im.common.common.packets.ChatBody;

public interface AysnChatService {
    /**
    * @description: 异步记录聊天记录
    * @param: [chatBody, command]
    * @return: void
    * @author: apollo
    * @date: 2019-09-18
    */
    void synAddChatRecord(ChatBody chatBody, int command);

    /**
    * @description: 异步修改聊天用户状态
    * @param: [userId, op]
    * @return: void
    * @author: apollo
    * @date: 2019-09-18
    */
    void synUpdateMember(String userId, int op);
}
