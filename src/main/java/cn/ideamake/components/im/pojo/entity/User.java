package cn.ideamake.components.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author ideamake
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_user")
public class User implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 经纪人唯一标识
     */
    private String uuid;

    /**
     * 所属项目编号
     */
    private String projectCode;

    /**
     * 当前角色表id
     */
    private Integer roleId;

    /**
     * 用户名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录token
     */
    private String loginToken;

    /**
     * 访问openid
     */
    private String openid;

    /**
     * 微信openid
     */
    private String wechatOpenid;

    /**
     * 微信unionid
     */
    private String wechatUnionid;

    /**
     * 是否正常使用状态，0：不，1：正常
     */
    private Integer isActive;

    /**
     * 离职状态，0：未离职，1：已离职
     */
    private Integer leaveStatus;

    /**
     * 审核状态，0：未审核，1：已审核
     */
    private Integer auditStatus;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别，1：男、2：女
     */
    private Integer gender;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 地区编号
     */
    private String regionCode;

    /**
     * 二维码链接
     */
    private String qrCode;

    /**
     * 是否项目用户，0：不是，1：是
     */
    private Boolean isProjectUser;

    /**
     * 备注
     */
    private String description;

    /**
     * 来源
     */
    private String channel;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
