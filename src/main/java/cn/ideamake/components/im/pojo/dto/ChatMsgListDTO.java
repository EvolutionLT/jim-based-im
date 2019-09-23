package cn.ideamake.components.im.pojo.dto;

import cn.ideamake.common.pojo.PageQueryBase;
import lombok.Data;

/**
 * @author evolution
 * @title: ChatUserListQuery
 * @projectName im
 * @description: TODO 查询用户聊天列表入参
 * @date 2019-07-08 11:42
 * @ltd：思为
 */
@Data
public class ChatMsgListDTO extends PageQueryBase {

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 查询未读消息类型
     */

    private String type;

    /**
     * 用户ID
     */
    private  String id;



}
