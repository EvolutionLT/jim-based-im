package cn.ideamake.components.im.pojo.dto;

import lombok.Data;

/**
 * @author Walt
 * @date 2019-09-17 15:38
 */
@Data
public class DeleteOfflineMessageDTO {

    private String token;
    private String userId;
    private String friendId;
}
