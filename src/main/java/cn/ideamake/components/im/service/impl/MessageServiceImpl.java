package cn.ideamake.components.im.service.impl;

import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.server.helper.redis.RedisMessageHelper;
import cn.ideamake.components.im.pojo.dto.DeleteOfflineMessageDTO;
import cn.ideamake.components.im.pojo.vo.UserFriendsVO;
import cn.ideamake.components.im.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Walt
 * @date 2019-09-16 23:52
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageHelper messageHelper;

    @Override
    public UserFriendsVO getUserFriendsVO(String friendId, String token) {
        String userId = RedisMessageHelper.checkToken(token);
        return messageHelper.getUserFriendSingle(userId,friendId);
    }

    @Override
    public boolean deleteOfflineMessage(DeleteOfflineMessageDTO deleteOfflineMessageDTO) {
        return false;
    }
}
