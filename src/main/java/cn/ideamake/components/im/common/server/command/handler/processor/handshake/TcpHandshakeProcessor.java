/**
 *
 */
package cn.ideamake.components.im.common.server.command.handler.processor.handshake;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.Protocol;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.HandshakeBody;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.tcp.TcpSessionContext;
import cn.ideamake.components.im.common.common.utils.ImKit;
import org.tio.core.ChannelContext;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午8:11:34
 */
public class TcpHandshakeProcessor implements HandshakeCmdProcessor {

    @Override
    public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
        RespBody handshakeBody = new RespBody(Command.COMMAND_HANDSHAKE_RESP, new HandshakeBody(Protocol.HANDSHAKE_BYTE));
        ImPacket handshakePacket = ImKit.ConvertRespPacket(handshakeBody, channelContext);
        return handshakePacket;
    }

    /**
     * 握手成功后
     * @param packet
     * @param channelContext
     * @throws Exception
     * @author Wchao
     */
    @Override
    public void onAfterHandshaked(ImPacket packet, ChannelContext channelContext) throws Exception {

    }

    @Override
    public boolean isProtocol(ChannelContext channelContext) {
        Object sessionContext = channelContext.getAttribute();
        if (sessionContext == null) {
            return false;
        } else if (sessionContext instanceof TcpSessionContext) {
            return true;
        }
        return false;
    }


    @Override
    public String name() {

        return Protocol.TCP;
    }

}
