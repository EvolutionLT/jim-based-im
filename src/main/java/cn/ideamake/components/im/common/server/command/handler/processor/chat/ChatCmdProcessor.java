package cn.ideamake.components.im.common.server.command.handler.processor.chat;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.server.command.handler.processor.CmdProcessor;
import org.tio.core.ChannelContext;

/**
 * 聊天请求cmd业务处理器接口
 *
 * @author WChao
 * @date 2018年4月2日 下午3:21:01
 */
public interface ChatCmdProcessor extends CmdProcessor {
    /**
     * 聊天cmd业务处理器处理方法;
     *
     * @param chatPacket
     * @param channelContext
     * @throws Exception
     */
    public void handler(ImPacket chatPacket, ChannelContext channelContext);
}
