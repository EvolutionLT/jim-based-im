/**
 *
 */
package cn.ideamake.components.im.common.common.tcp;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.Protocol;
import cn.ideamake.components.im.common.common.protocol.AbProtocol;
import cn.ideamake.components.im.common.common.protocol.IConvertProtocolPacket;
import cn.ideamake.components.im.common.common.tcp.TcpConvertPacket;
import cn.ideamake.components.im.common.common.tcp.TcpPacket;
import cn.ideamake.components.im.common.common.tcp.TcpSessionContext;
import cn.ideamake.components.im.common.common.utils.ImUtils;
import org.tio.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 * Tcp协议判断器
 * @author WChao
 *
 */
public class TcpProtocol extends AbProtocol {

    @Override
    public String name() {
        return Protocol.TCP;
    }

    @Override
    public boolean isProtocolByBuffer(ByteBuffer buffer, ChannelContext channelContext) throws Throwable {
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        if (imSessionContext != null && imSessionContext instanceof TcpSessionContext) {
            return true;
        }
        if (buffer != null) {
            //获取第一个字节协议版本号;
            byte version = buffer.get();
            //TCP协议;
            if (version == Protocol.VERSION) {
                channelContext.setAttribute(new TcpSessionContext());
                ImUtils.setClient(channelContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public IConvertProtocolPacket converter() {
        return new TcpConvertPacket();
    }

    @Override
    public boolean isProtocol(ImPacket imPacket, ChannelContext channelContext) throws Throwable {
        if (imPacket == null) {
            return false;
        }
        if (imPacket instanceof TcpPacket) {
            Object sessionContext = channelContext.getAttribute();
            if (sessionContext == null) {
                channelContext.setAttribute(new TcpSessionContext());
            }
            return true;
        }
        return false;
    }
}
