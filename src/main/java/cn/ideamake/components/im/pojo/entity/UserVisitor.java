package cn.ideamake.components.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 置业顾问-客户关联表
 * </p>
 *
 * @author ideamake
 * @since 2019-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_user_visitor")
public class UserVisitor implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 访客代码
     */
    private String visitorCode;

    /**
     * 男-1，女-2
     */
    private Integer gender;

    /**
     * 客户姓名(备注)
     */
    private String visitorName;

    /**
     * 客户小程序唯一id
     */
    private String openId;
    private String appId;

    private String unionId;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 置业顾问or渠道中介uuid
     */
    private String userUuid;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 当前状态 0无效  1初步报备、2已到访、。。。。。
     */
    private Integer status;

    /**
     * 客户类型：0渠道中介指定顾问，1线上报备，2自然到访
     */
    private Integer type;

    /**
     * 1渠道中介，0置业顾问
     */
    private Integer userType;

    /**
     * 备注
     */
    private String remark;

    /**
     * {"visit":"" ,"submit":"" ,"success":""}
     */
    private String schedule;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;


}
