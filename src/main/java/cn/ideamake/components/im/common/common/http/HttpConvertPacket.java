/**
 *
 */
package cn.ideamake.components.im.common.common.http;

import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.http.HttpRequest;
import cn.ideamake.components.im.common.common.http.HttpResponse;
import cn.ideamake.components.im.common.common.http.session.HttpSession;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.protocol.IConvertProtocolPacket;
import org.tio.core.ChannelContext;

/**
 * HTTP协议消息转化包
 * @author WChao
 *
 */
public class HttpConvertPacket implements IConvertProtocolPacket {

    /**
     * 转HTTP协议响应包;
     */
    @Override
    public ImPacket RespPacket(byte[] body, Command command, ChannelContext channelContext) {
        Object sessionContext = channelContext.getAttribute();
        if (sessionContext instanceof HttpSession) {
            HttpRequest request = (HttpRequest) channelContext.getAttribute(ImConst.HTTP_REQUEST);
            HttpResponse response = new HttpResponse(request, request.getHttpConfig());
            response.setBody(body, request);
            response.setCommand(command);
            return response;
        }
        return null;
    }

    @Override
    public ImPacket ReqPacket(byte[] body, Command command, ChannelContext channelContext) {

        return null;
    }

}
