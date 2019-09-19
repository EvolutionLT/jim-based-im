package cn.ideamake.components.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 访客表
 * </p>
 *
 * @author ideamake
 * @since 2019-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_visitor")
public class Visitor implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * 微信openid
     */
    @TableId(value = "openid")
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 置业顾问uuid
     */
    private String userUuid;

    /**
     * 登录token
     */
    private String loginToken;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 性别，1：男，2：女
     */
    private Integer gender;

    /**
     * 角色 1：访客 2：独立经纪人
     */
    private Integer role;

    /**
     * 全民经纪人类型 0：访客，1： 五矿业主 2：五矿员工
     */
    private Integer type;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 身份证图片，证明
     */
    private String cardImgPositive;

    /**
     * 身份证图片，反面
     */
    private String cardImgReverse;

    /**
     * 访客扩展信息
     */
    private String expand;

    /**
     * 全民经纪人认证状态，审核状态，0未通过审核，1审核中，2审核通过
     */
    private Integer verifyStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


    private Integer departmentId;



}
