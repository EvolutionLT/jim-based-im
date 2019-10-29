package cn.ideamake.components.im.pojo.vo;

import lombok.Data;

/**
 * @author evolution
 * @title: ChatListVo
 * @projectName im
 * @description: TODO
 * @date 2019-07-08 11:12
 * @ltd：思为
 */
@Data
public class ChatUserListVO {


    /**
     * 用户ID
     */
    private String userId;
    /**
     * 昵称
     */
    private String nick;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 最近消息
     */
    private String msgContent;
    /**
     * 消息时间
     */
    private String date;
    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 是否在线
     */

    private Object isOnline;

    /**
     * 服务人数
     */
    private String serviceCount;

    /**
     * 未读消息
     */
    private String notRead;

    /**
     * 用户类型 0=客服 1=访客 2=置业顾问
     */
    private int userType;


    private String time;

    private String createdAt;





}
