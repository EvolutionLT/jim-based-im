package cn.ideamake.components.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author evolution
 * @since 2019-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_im_user")
public class IMUsers implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 签名
     */
    private String sign;

    /**
     * 咨询人数
     */
    private String serviceCount;

    /**
     * 回复速度
     */
    private String recoverySpeed;

    /**
     * 满意度
     */
    private String degree;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 身份 0置业顾问  1 客户  2 全民经纪人  3 其他用户
     */
    private String capacity;


    /**
     * UUID
     */
    private String uuid;
    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 修改时间
     */
    private LocalDateTime updateAt;



}
