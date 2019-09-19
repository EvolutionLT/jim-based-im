package cn.ideamake.components.im.service.vanke;

import cn.ideamake.components.im.common.common.packets.ChatBody;

public interface VankeMessageService {
    /**
    * @description: 异步把聊天消息写到Mysql
    * @param: [chatBody]
    * @return: void
    * @author: apollo
    * @date: 2019-09-17
    */
    void writeMessage(ChatBody chatBody, int command);

    /**
    * @description: 建立连接的时候初始化聊天成员
    * @param: [userId, ip]
    * @return: void
    * @author: apollo
    * @date: 2019-09-18
    */
    void updateChatMember(String userId, int op);
}
