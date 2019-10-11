package cn.ideamake.components.im.common.server.command.handler;

import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.pojo.constant.UserType;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import cn.ideamake.components.im.common.server.command.handler.processor.chat.ChatCmdProcessor;
import cn.ideamake.components.im.common.server.command.handler.processor.chat.MsgQueueRunnable;
import org.tio.core.ChannelContext;

import java.util.List;
import java.util.Optional;

/**
 * 版本: [1.0]
 * 功能说明: 聊天请求cmd消息命令处理器
 *
 * @author : WChao 创建时间: 2017年9月22日 下午2:58:59
 */
public class ChatReqHandler extends AbstractCmdHandler {

    @Override
    public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        if (packet.getBody() == null) {
            throw new Exception("body is null");
        }
        ChatBody chatBody = ChatKit.toChatBody(packet.getBody(), channelContext);
        packet.setBody(chatBody.toByte());
        //聊天数据格式不正确
        if (chatBody == null || chatBody.getChatType() == null) {
            ImPacket respChatPacket = ChatKit.dataInCorrectRespPacket(channelContext);
            return respChatPacket;
        }
        List<ChatCmdProcessor> chatProcessors = this.getProcessorNotEqualName(Sets.newHashSet(ImConst.BASE_ASYNC_CHAT_MESSAGE_PROCESSOR), ChatCmdProcessor.class);
        if (CollectionUtils.isNotEmpty(chatProcessors)) {
            chatProcessors.forEach(chatProcessor -> chatProcessor.handler(packet, channelContext));
        }
        //异步调用业务处理消息接口
        if (ChatType.forNumber(chatBody.getChatType()) != null) {
            MsgQueueRunnable msgQueueRunnable = (MsgQueueRunnable) channelContext.getAttribute(ImConst.CHAT_QUEUE);
            msgQueueRunnable.addMsg(packet);
            msgQueueRunnable.getExecutor().execute(msgQueueRunnable);
        }
        ImPacket chatPacket = new ImPacket(Command.COMMAND_CHAT_REQ, new RespBody(Command.COMMAND_CHAT_REQ, chatBody).toByte());
        //设置同步序列号;
        chatPacket.setSynSeq(packet.getSynSeq());
        //私聊
        String toId = chatBody.getTo();
        String fromId = chatBody.getFrom();
        if (ChatType.CHAT_TYPE_PRIVATE.getNumber() == chatBody.getChatType()) {
            if (ChatKit.isOnline(toId, imConfig)) {
                ImAio.sendToUser(toId, chatPacket);
                //发送成功响应包
                return ChatKit.sendSuccessRespPacket(channelContext);
            } else {
                //发送指定消息,告知发送人，接收方不在线
                User user = RedisCacheManager.getCache(ImConst.USER).get(toId + ":" + Constants.USER.INFO, User.class);
                Optional.ofNullable(UserType.getUserType(user.getType())).map(e -> {
                    String message = e.getMessage();
                    ImAio.sendToUser(user, fromId, message, "true");
                    return e;
                });
                //用户不在线响应包
                return ChatKit.offlineRespPacket(channelContext);
            }
            //群聊
        } else if (ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()) {
            String groupId = chatBody.getGroupId();
            ImAio.sendToGroup(groupId, chatPacket);
            //发送成功响应包
            return ChatKit.sendSuccessRespPacket(channelContext);
        }
        return null;
    }

    @Override
    public Command command() {
        return Command.COMMAND_CHAT_REQ;
    }
}
