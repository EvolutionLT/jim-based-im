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
 * @create: 2019/09/18 09:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_cus_chat_room_relate")
public class CusChatRoomRelate implements Serializable {
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
     * userId
     */
    private String userId;

    /**
     * 类型: 1=发送方，0=接收方
     */
    private Integer type;

    /**
     * chat_member_id主键
     */
    private Integer chatMemberId;


    /**
     * 状态,0=有效, 1=无效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}
