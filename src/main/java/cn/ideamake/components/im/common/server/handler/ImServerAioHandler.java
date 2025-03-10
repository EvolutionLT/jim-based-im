package cn.ideamake.components.im.common.server.handler;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.server.ImServerGroupContext;
import cn.ideamake.components.im.common.server.command.handler.processor.chat.MsgQueueRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;

/**
 * @author WChao
 */
@Service
@Slf4j
public class ImServerAioHandler implements ServerAioHandler {

    private ImConfig imConfig;

//	public ImServerAioHandler(ImConfig imConfig) {
//		this.imConfig = imConfig;
//	}

    /**
     * @param packet
     * @return
     * @throws Exception
     * @author: Wchao
     * 2016年11月18日 上午9:37:44
     * @see org.tio.core.intf.AioHandler#handler(Packet)
     */
    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        AbstractProtocolHandler handler = (AbstractProtocolHandler) imSessionContext.getProtocolHandler();
        if (handler != null) {
            handler.handler(packet, channelContext);
        }
    }

    /**
     * @param packet
     * @return
     * @author: Wchao
     * 2016年11月18日 上午9:37:44
     * @see org.tio.core.intf.AioHandler#encode(Packet)
     */
    @Override
    public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        AbstractProtocolHandler handler = (AbstractProtocolHandler) imSessionContext.getProtocolHandler();
        if (handler != null) {
            return handler.encode(packet, groupContext, channelContext);
        }
        return null;
    }

    /**
     * @param buffer
     * @return
     * @throws AioDecodeException
     * @author: Wchao
     * 2016年11月18日 上午9:37:44
     * @see org.tio.core.intf.AioHandler#decode(ByteBuffer)
     */
    @Override
    public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        AbstractProtocolHandler handler = null;
        if (imSessionContext == null) {
            handler = ProtocolHandlerManager.initServerHandlerToChannelContext(buffer, channelContext);
            ImServerGroupContext imGroupContext = (ImServerGroupContext) imConfig.getGroupContext();
            channelContext.setAttribute(ImConst.CHAT_QUEUE, new MsgQueueRunnable(channelContext, imGroupContext.getTimExecutor()));
        } else {
            handler = (AbstractProtocolHandler) imSessionContext.getProtocolHandler();
        }
        if (handler != null) {
            return handler.decode(buffer, channelContext);
        } else {
            throw new AioDecodeException("不支持的协议类型,无法找到对应的协议解码器!");
        }
    }

    public ImConfig getImConfig() {
        return imConfig;
    }

    public void setImConfig(ImConfig imConfig) {
        this.imConfig = imConfig;
    }

}
