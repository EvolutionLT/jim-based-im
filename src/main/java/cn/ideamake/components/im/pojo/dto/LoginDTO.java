package cn.ideamake.components.im.pojo.dto;

import lombok.Data;

/**
 * 用户登录校验类
 */
@Data
public class LoginDTO {
    private String userId;
    private String password;
    private String token;
}
