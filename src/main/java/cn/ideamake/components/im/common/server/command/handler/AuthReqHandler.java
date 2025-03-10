package cn.ideamake.components.im.common.server.command.handler;

import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.packets.AuthReqBody;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.utils.ImKit;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import org.tio.core.ChannelContext;

/**
 * 版本: [1.0]
 * 功能说明: 鉴权请求消息命令处理器
 *
 * @author : WChao 创建时间: 2017年9月13日 下午1:39:35
 */
public class AuthReqHandler extends AbstractCmdHandler {
    @Override
    public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        if (packet.getBody() == null) {
            RespBody respBody = new RespBody(Command.COMMAND_AUTH_RESP, ImStatus.C10010);
            return ImKit.ConvertRespPacket(respBody, channelContext);
        }
        AuthReqBody authReqBody = JsonKit.toBean(packet.getBody(), AuthReqBody.class);
        String token = authReqBody.getToken() == null ? "" : authReqBody.getToken();
        String data = token + ImConst.AUTH_KEY;
        authReqBody.setToken(data);
        authReqBody.setCmd(null);
        RespBody respBody = new RespBody(Command.COMMAND_AUTH_RESP, ImStatus.C10009).setData(authReqBody);
        return ImKit.ConvertRespPacket(respBody, channelContext);
    }

    @Override
    public Command command() {
        return Command.COMMAND_AUTH_REQ;
    }
}
