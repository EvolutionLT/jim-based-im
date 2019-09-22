package cn.ideamake.components.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @program wk-ww-cs
 * @description:
 * @author: apollo
 * @create: 2019/09/18 10:02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_cus_chat_message")
public class CusChatMessage implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * wk_cus_chat_room主键
     */
    private Integer chatRoomId;

    /**
     * 消息id
     *
     */
    private String messageId;

    /**
     * 消息类型 0:text、1:image、2:voice、3:vedio、4:music、5:news
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 聊天方式(私聊，公聊)
     */
    private Integer chatType;

    /**
     * 消息发送方id(可能是访客也可能是客服)
     */
    private String senderId;

    /**
     * 接收人id
     */
    private String receiverId;

    /**
     * 状态, 1=删除 0=有效, 2=撤回
     */
    private Integer status;

    /**
     * 发送时间
     */
    private Long sendTime;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}
