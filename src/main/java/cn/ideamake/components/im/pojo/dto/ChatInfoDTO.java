package cn.ideamake.components.im.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program jio-based-im
 * @description:
 * @author: apollo
 * @create: 2019/09/26 15:50
 */
@Data
public class ChatInfoDTO implements Serializable {
    @NotBlank
    private String visitorId;

    @NotBlank
    private String cusId;

    /**
     * 操作 1=增加 2删除
     */
    @NotNull
    private Integer op;

    /**
     * 是否是新成员 0=好友，1=新人
     */
    @NotNull
    private Integer isNewMember;

    /**
     * 是否是当前聊天成员 0=不是 1=是
     */
    @NotNull
    private Integer isConcurrent;
}
