package cn.ideamake.components.im.pojo.dto;

import cn.ideamake.components.im.common.common.packets.Group;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @program jio-based-im
 * @description:
 * @author: apollo
 * @create: 2019/09/17 17:38
 */
@Data
public class VankeLoginDTO implements Serializable {
    /**
     * 发送用户id;
     */
    @NotBlank
    private String senderId;

    /**
     * 接收用户id;
     */
    private String receiverId;

    /**
     * 发送人token
     */
    @NotBlank
    private String token;

    /**
     * 发送人昵称
     */
    @NotBlank
    private String nick;

    /**
     * 发送人用户头像
     */
    @NotBlank
    private String avatar;

    /**
     * 发送人用户所属终端
     */
    @NotBlank
    private String terminal;

    /**
     * 发送人身份类型,0=客服,1=访客, 2置业顾问
     */
    @NotNull
    private Integer type;

    /**
     * 个性签名;
     */
    private String sign;

    /**
     * 项目code
     */
    private String projectCode;

}
