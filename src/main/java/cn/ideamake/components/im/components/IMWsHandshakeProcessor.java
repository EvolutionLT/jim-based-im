/**
 *
 */
package cn.ideamake.components.im.components;

import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.http.HttpConst;
import cn.ideamake.components.im.common.common.http.HttpRequest;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.LoginReqBody;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.command.handler.LoginReqHandler;
import cn.ideamake.components.im.common.server.command.handler.processor.handshake.WsHandshakeProcessor;
import lombok.extern.slf4j.Slf4j;

import org.tio.core.ChannelContext;

/**
 * @author Walt
 * ws连接握手，在此处校验用户token
 */
@Slf4j
public class IMWsHandshakeProcessor extends WsHandshakeProcessor {


    @Override
    public void onAfterHandshaked(ImPacket packet, ChannelContext channelContext) throws Exception {
        log.info("握手阶段");
        LoginReqHandler loginHandler = (LoginReqHandler) CommandManager.getCommand(Command.COMMAND_LOGIN_REQ);
        HttpRequest request = (HttpRequest) packet;
        String username = request.getParams().get("username") == null ? null : (String) request.getParams().get("username")[0];
        String password = request.getParams().get("password") == null ? null : (String) request.getParams().get("password")[0];
        String token = request.getParams().get("token") == null ? null : (String) request.getParams().get("token")[0];
        LoginReqBody loginBody = new LoginReqBody(username, password, token);
        byte[] loginBytes = JsonKit.toJsonBytes(loginBody);
        request.setBody(loginBytes);
        request.setBodyString(new String(loginBytes, HttpConst.CHARSET_NAME));
        ImPacket loginRespPacket = loginHandler.handler(request, channelContext);
        if (loginRespPacket != null) {
            ImAio.send(channelContext, loginRespPacket);
        }
        log.info("握手结束");
    }
}
