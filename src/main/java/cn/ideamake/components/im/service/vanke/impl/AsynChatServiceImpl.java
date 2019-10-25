package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.service.vanke.AysnChatService;
import cn.ideamake.components.im.service.vanke.VankeMessageService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @program jio-based-im
 * @description: AysnChatService
 * @author: apollo
 * @create: 2019/09/18 10:12
 */
@Service
@Slf4j
public class AsynChatServiceImpl implements AysnChatService {

    @Resource
    private Executor vankeExecutor;

    @Resource
    private VankeMessageService vankeMessageService;

    @Override
    public void synAddChatRecord(ChatBody chatBody, int command) {
        CompletableFuture.runAsync(() -> vankeMessageService.writeMessage(chatBody, command), vankeExecutor).exceptionally(throwable -> {
            log.error("异步记录聊天信息错误，chatBody: {}, 错误信息: ", JSON.toJSONString(chatBody), throwable);
            return null;
        });
    }


    @Override
    public void synUpdateMember(String userId, int op) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        CompletableFuture.runAsync(() -> vankeMessageService.updateChatMember(userId, op), vankeExecutor).exceptionally(e -> {
            log.error("异步更新用户状态异常，userId: {}, op: {} error: ", userId, op, e);
            return null;
        });
    }

    @Override
    public void synInitChatMember(VankeLoginDTO dto) {
        if (!Objects.isNull(dto)) {
            CompletableFuture.runAsync(() -> vankeMessageService.initMember(dto), vankeExecutor).exceptionally(e -> {
                log.error("异步初始化member信息错误,dto: {}, error: ", JSON.toJSONString(dto), e);
                return null;
            });
        }
    }

    @Override
    public void synInitChatInfo(VankeLoginDTO dto) {
        if (!Objects.isNull(dto)) {
            CompletableFuture.runAsync(() -> vankeMessageService.initChatInfo(dto), vankeExecutor).exceptionally(e -> {
                log.error("异步初始化聊天信息错误,dto: {}, error: ", JSON.toJSONString(dto), e);
                return null;
            });
        }
    }

    @Override
    public void synDelFriend(String cusId, String friendId) {

    }
}
