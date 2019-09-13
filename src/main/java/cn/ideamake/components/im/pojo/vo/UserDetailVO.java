package cn.ideamake.components.im.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Walt
 * @date 2019-09-13 10:41
 * 拉取用户详情时返回给前端内容，包括用户基本信息和好友聊天信息
 * 后续会补充群组聊天信息
 */
@Data
public class UserDetailVO {
    private String nickname;
    private String userId;
    /**
     * 用户好友
     */
    private List<UserFriendsVO> friends;
}
