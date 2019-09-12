package cn.ideamake.components.im.pojo.dto;

import lombok.Data;

/**
 * @author Walt
 * @date 2019-09-12 11:51
 */
@Data
public class UserInfoDTO {
    private String avatar;
    private String isRegisterSale;
    private String country;
    private String gender;
    private String province;
    private String phone;
    private String city;
    private String openId;
    private Integer isLoginBefore;
    private String nickname;
    private String loginPhone;
    private Integer isRegisterDs;
    private Integer isBindSdWx;
}
