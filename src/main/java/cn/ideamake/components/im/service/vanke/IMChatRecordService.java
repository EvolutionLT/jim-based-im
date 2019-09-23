package cn.ideamake.components.im.service.vanke;

import cn.ideamake.common.response.Result;
import cn.ideamake.components.im.pojo.dto.ChatMsgListDTO;
import cn.ideamake.components.im.pojo.entity.IMChatRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evolution
 * @since 2019-07-07
 */
public interface IMChatRecordService extends IService<IMChatRecord> {
    /**
     * 新增聊天记录
     * @param IMChatRecord
     * @return
     */
    int insertChatRecord(IMChatRecord IMChatRecord);

    /**
     * 根据roomid获取聊天信息
     * @param chatMsgListQuery
     * @return
     */
    Result getMsgList(ChatMsgListDTO chatMsgListQuery);

    /**
     * 修改消息状态
     * @param chatMsgListQuery
     */
    Result updateChatRecordStatus(ChatMsgListDTO chatMsgListQuery);


    /**
     *
     * @param chatMsgListQuery type 0 查当前用户所有未读消息  1 查询单个房间未读消息
     * @return
     */
    Result  getUserMsgNotRead(ChatMsgListDTO chatMsgListQuery);
}
