package cn.ideamake.components.im.common.server.command.handler.processor.chat;

import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.command.handler.ChatReqHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

import java.util.concurrent.Executor;

/**
 * @author WChao
 * @date 2018年4月3日 上午10:47:40
 */
public class MsgQueueRunnable extends AbstractQueueRunnable<ImPacket> {

    private Logger log = LoggerFactory.getLogger(cn.ideamake.components.im.common.server.command.handler.processor.chat.MsgQueueRunnable.class);

    private ChannelContext channelContext = null;

    private AsyncChatMessageProcessor chatMessageProcessor;

    @Override
    public boolean addMsg(ImPacket msg) {
        if (this.isCanceled()) {
            log.error("{}, 任务已经取消，{}添加到消息队列失败" , channelContext, msg);
            return false;
        }
        return msgQueue.add(msg);
    }

    public MsgQueueRunnable(ChannelContext channelContext, Executor executor) {
        super(executor);
        this.channelContext = channelContext;
        ChatReqHandler chatReqHandler = CommandManager.getCommand(Command.COMMAND_CHAT_REQ, ChatReqHandler.class);
        chatMessageProcessor = chatReqHandler.getProcessor(ImConst.BASE_ASYNC_CHAT_MESSAGE_PROCESSOR, BaseAsyncChatMessageProcessor.class).get(0);
    }

    @Override
    public void runTask() {
        int queueSize = msgQueue.size();
        if (queueSize == 0) {
            return;
        }
        ImPacket packet = null;
        while ((packet = msgQueue.poll()) != null) {
            if (chatMessageProcessor != null) {
                try {
                    ChatBody chatBody = ChatKit.toChatBody(packet.getBody(), channelContext);
                    chatMessageProcessor.handler(chatBody, channelContext);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }
        }
    }
}
