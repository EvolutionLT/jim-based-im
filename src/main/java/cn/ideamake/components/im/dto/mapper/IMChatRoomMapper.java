package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMChatRoom;

import cn.ideamake.components.im.pojo.vo.ChatUserListVO;
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
public interface IMChatRoomMapper extends BaseMapper<IMChatRoom> {
    /**
     * 根据用户ID查询已经聊天用户对话列表
     */
    List getAllChatUserList(IPage page, @Param("query") ChatUserListDTO chatUserListQuery);

    /**
     * 查询单个用户房间信息
     * @param id to
     * @return
     */
    ChatUserListVO getChatRoomInfo(@Param("id") String id, @Param("to") String to);

    /**
     * 根据用户ID查询已经聊天用户对话列表
     */
    List getAllChatUserLists(@Param("id") String id);

    /**
     * 根据id查询该顾问服务咨询次数
     * @param id
     * @return
     */
    int  getUserServiceCount(@Param("id") String id);

    /**
     * 查询房间是否存在
     * @param id
     * @return
     */
    String getIsRoom(String id, String user_id);

    /**
     * 删除用户对话
     */
    int deleteRoom(@Param("roomId") String roomId);

}
