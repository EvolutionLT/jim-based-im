package cn.ideamake.components.im.service;

import cn.ideamake.components.im.pojo.dto.DeleteOfflineMessageDTO;
import cn.ideamake.components.im.pojo.vo.UserFriendsVO;

/**
 * @author Walt
 * @date 2019-09-16 23:50
 */
public interface MessageService {

    /**
     * 获取单个好友的详细信息
     * @param friendId
     * @param token
     * @return
     */
    UserFriendsVO getUserFriendsVO(String friendId,String token);


    /**
     * 删除用户离线消息队列
     * @param deleteOfflineMessageDTO
     * @return
     */
    boolean deleteOfflineMessage(DeleteOfflineMessageDTO deleteOfflineMessageDTO);
}
