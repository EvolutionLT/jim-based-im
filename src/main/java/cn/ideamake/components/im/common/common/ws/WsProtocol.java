/**
 *
 */
package cn.ideamake.components.im.common.common.ws;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.Protocol;
import cn.ideamake.components.im.common.common.http.HttpConst;
import cn.ideamake.components.im.common.common.http.HttpRequest;
import cn.ideamake.components.im.common.common.http.HttpRequestDecoder;
import cn.ideamake.components.im.common.common.protocol.AbProtocol;
import cn.ideamake.components.im.common.common.protocol.IConvertProtocolPacket;
import cn.ideamake.components.im.common.common.utils.ImUtils;
import cn.ideamake.components.im.common.common.ws.WsConvertPacket;
import cn.ideamake.components.im.common.common.ws.WsPacket;
import cn.ideamake.components.im.common.common.ws.WsSessionContext;
import org.tio.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 * WebSocket协议判断器
 * @author WChao
 *
 */
public class WsProtocol extends AbProtocol {

    @Override
    public String name() {
        return Protocol.WEBSOCKET;
    }

    @Override
    public boolean isProtocolByBuffer(ByteBuffer buffer, ChannelContext channelContext) throws Throwable {
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        if (imSessionContext != null && imSessionContext instanceof WsSessionContext) {
            return true;
        }
        //第一次连接;
        if (buffer != null) {
            HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext, false);
            if (request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) != null) {
                channelContext.setAttribute(new WsSessionContext());
                ImUtils.setClient(channelContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public IConvertProtocolPacket converter() {
        return new WsConvertPacket();
    }

    @Override
    public boolean isProtocol(ImPacket imPacket, ChannelContext channelContext) throws Throwable {
        if (imPacket == null) {
            return false;
        }
        if (imPacket instanceof WsPacket) {
            Object sessionContext = channelContext.getAttribute();
            if (sessionContext == null) {
                channelContext.setAttribute(new WsSessionContext());
            }
            return true;
        }
        return false;
    }
}
