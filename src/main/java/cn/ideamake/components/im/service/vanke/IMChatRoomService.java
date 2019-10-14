package cn.ideamake.components.im.service.vanke;

import cn.ideamake.common.response.Result;
import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMChatRoom;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evolution
 * @since 2019-07-07
 */
public interface IMChatRoomService extends IService<IMChatRoom> {

    /**
     * 新增房间
     * @param chatRoom
     * @return
     */
    Result insertChatRoom(IMChatRoom chatRoom);

    /**
     * 根据用户ID查询已经聊天用户对话列表
     */
    Result getAllChatUserList(ChatUserListDTO chatUserListQuery);

    /**
     * 查询房间是否存在
     * @param id 用户id
     * @param user_id 其他id
     * @return
     */
    Result getIsRoom(String id, String user_id);

    /**
     * 删除用户对话
     */
    Result deleteRoom(ChatUserListDTO chatUserListQuery);

}
