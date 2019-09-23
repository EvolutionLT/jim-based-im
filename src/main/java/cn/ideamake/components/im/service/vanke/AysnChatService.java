package cn.ideamake.components.im.service.vanke;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;

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

    /**
     * @description: 异步初始化聊天成员信息
     * @param: [dto]
     * @return: void
     * @author: apollo
     * @date: 2019-09-23
     */
    void synInitChatMember(VankeLoginDTO dto);


    /**
     * @description: 异步初始化聊天相关信息
     * @param: [dto]
     * @return: void
     * @author: apollo
     * @date: 2019-09-23
     */
    void synInitChatInfo(VankeLoginDTO dto);
}
