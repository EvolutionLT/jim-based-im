package cn.ideamake.components.im.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Walt
 * @date 2019-09-14 18:34
 * 管理员操作组时需要传递的参数
 */
@Data
public class UserGroupDTO {
    @NotNull(message = "被邀请的用户id不能为空")
    private String userId;
    @NotNull(message = "邀请的组id不能为空")
    private String groupId;
    @NotNull(message = "请求token不能为空")
    private String token;
}
