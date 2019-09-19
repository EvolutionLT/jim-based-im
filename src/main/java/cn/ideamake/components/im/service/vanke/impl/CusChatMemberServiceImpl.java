package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.service.vanke.CusChatMemberService;
import cn.ideamake.components.im.service.vanke.VankeMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @program jio-based-im
 * @description: CusChatMemberService
 * @author: apollo
 * @create: 2019/09/18 10:12
 */
@Service
public class CusChatMemberServiceImpl implements CusChatMemberService {

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
}
