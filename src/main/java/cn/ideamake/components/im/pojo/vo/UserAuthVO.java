package cn.ideamake.components.im.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Walt
 * @date 2019-09-12 15:09
 */
@Data
@AllArgsConstructor
public class UserAuthVO {
    private String token;
    private String userId;
}
