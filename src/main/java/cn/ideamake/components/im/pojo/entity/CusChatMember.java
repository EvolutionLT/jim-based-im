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
@TableName("wk_cus_chat_member")
public class CusChatMember implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 聊天成员Id
     */
    private String userId;

    /**
     * token
     */
    private String token;

    /**
     * 客服昵称
     */
    private String nickName;

    /**
     * 客服电话号码
     */
    private String phone;

    /**
     * 头像
     */
    private String headImgUrl;

    /**
     * 身份类型,0=客服,1=访客, 2置业顾问
     */
    private Integer type;

    /**
     * 状态,0=有效, 1=无效
     */
    private Integer status;

    /**
     * 是否繁忙 0=空闲， 1=繁忙
     */
    private Integer isBusy;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}
