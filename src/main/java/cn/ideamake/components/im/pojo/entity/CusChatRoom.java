package cn.ideamake.components.im.pojo.entity;

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
 * @create: 2019/09/18 09:56
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_cus_chat_room")
public class CusChatRoom implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 状态 0=无效, 1=生效中，2=已关闭
     */
    private Integer status;

    /**
     * 聊天房间名称
     */
    private String name;

    /**
     * 客服openId
     */
    private String cusId;

    /**
     * 房间唯一编码
     */
    private String uniqueCode;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;
}
