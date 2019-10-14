package cn.ideamake.components.im.pojo.dto;

import cn.ideamake.common.pojo.PageQueryBase;
import lombok.Data;

/**
 * @author evolution
 * @title: ChatUserListQuery
 * @projectName im
 * @description: TODO 查询聊天用户列表入参
 * @date 2019-07-08 11:42
 * @ltd：思为
 */
@Data
public class ChatUserListDTO extends PageQueryBase {

    /**
     * 查询用户ID
     */
    private String uuid;

    /**
     * 第二个用户ID
     */
    private String to;

    /**
     * 创建人
     */
    private String from;
    /**
     * roomid 房间号
     *
     */
    private String roomId;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 用户类型
     */
    private  String userType;


}
