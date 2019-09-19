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
 * @description: 客服信息
 * @author: apollo
 * @create: 2019/09/16 11:22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_cus_info")
public class CusInfo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * UUID
     */
    private String uuId;

    /**
     * 分配权重
     */
    private Integer priority;

    /**
     * loginToken
     */
    private String loginToken;

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
     * openId
     */
    private String openId;

    /**
     * unionId
     */
    private String unionId;

    /**
     * 状态, 0=无效, 1=有效
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}
