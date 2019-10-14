package cn.ideamake.components.im.durables;


import cn.ideamake.components.im.common.common.packets.ChatBody;

/**
 * @author evolution
 * @title: MsgCrudStrategy
 * @projectName im
 * @description: TODO 消息数据持久化处理接口
 * @date 2019-07-07 10:35
 * @ltd：思为
 */
public interface DataCrudStrategy {
    /**
     * 插入消息记录表以及房间表
     */
    int insertMsgData(ChatBody chatBody);

}
