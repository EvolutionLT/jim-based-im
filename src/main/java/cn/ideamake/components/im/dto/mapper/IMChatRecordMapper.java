package cn.ideamake.components.im.dto.mapper;

;
import cn.ideamake.components.im.pojo.dto.ChatMsgListDTO;
import cn.ideamake.components.im.pojo.entity.IMChatRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author evolution
 * @since 2019-07-07
 */
@Repository
public interface IMChatRecordMapper extends BaseMapper<IMChatRecord> {
    /**
     * 根据roomid获取聊天信息
     * @param chatMsgListQuery
     * @param page 当前页
     * @return list集合
     */
    List getMsgList(IPage page, @Param("query") ChatMsgListDTO chatMsgListQuery);

    /**
     * 修改消息状态
     * @param chatMsgListQuery
     */
    void updateChatRecordStatus(@Param("query") ChatMsgListDTO chatMsgListQuery);

    /**
     *
     * @param room_id
     * @param type 0 查当前用户所有未读消息  1 查询单个房间未读消息
     * @return
     */
    int  getUserMsgNotRead(@Param("roomId") String room_id, @Param("type") String type, @Param("id") String id);

    int  deletechatRecord(@Param("roomId") String roomId);
}
