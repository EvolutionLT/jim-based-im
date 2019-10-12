package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.service.vanke.AysnChatService;
import cn.ideamake.components.im.service.vanke.VankeMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @program jio-based-im
 * @description: AysnChatService
 * @author: apollo
 * @create: 2019/09/18 10:12
 */
@Service
public class AsynChatServiceImpl implements AysnChatService {

    @Resource
    private Executor vankeExecutor;

    @Resource
    private VankeMessageService vankeMessageService;

    @Override
    public void synAddChatRecord(ChatBody chatBody, int command) {
        CompletableFuture.runAsync(() -> vankeMessageService.writeMessage(chatBody, command), vankeExecutor);
    }


    @Override
    public void synUpdateMember(String userId, int op) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        CompletableFuture.runAsync(() -> vankeMessageService.updateChatMember(userId, op), vankeExecutor);
    }

    @Override
    public void synInitChatMember(VankeLoginDTO dto) {
        if(!Objects.isNull(dto)) {
            CompletableFuture.runAsync(() -> vankeMessageService.initMember(dto), vankeExecutor);
        }
    }

    @Override
    public void synInitChatInfo(VankeLoginDTO dto) {
        if(!Objects.isNull(dto)) {
            CompletableFuture.runAsync(() -> vankeMessageService.initChatInfo(dto), vankeExecutor);
        }
    }

    @Override
    public void synDelFriend(String cusId, String friendId) {

    }
}
