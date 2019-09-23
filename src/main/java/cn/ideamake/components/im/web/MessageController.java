package cn.ideamake.components.im.web;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Walt
 * @date 2019-09-16 23:49
 */
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取单个好友的基本信息和第一条聊天纪录
     * @param friendId
     * @param token
     * @return
     */
    @PostMapping("/user/friend")
    public Rest getUserFriends(String friendId, String token){
        return Rest.okObj(messageService.getUserFriendsVO(friendId,token));
    }


    public Rest deleteOfflineMessage(String userId,String friendId,String token){
        return Rest.okObj("");
    }
}
