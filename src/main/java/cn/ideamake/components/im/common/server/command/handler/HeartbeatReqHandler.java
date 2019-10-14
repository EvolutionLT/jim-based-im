package cn.ideamake.components.im.common.server.command.handler;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.Protocol;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.HeartbeatBody;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.utils.ImKit;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import org.tio.core.ChannelContext;

/**
 *
 */
public class HeartbeatReqHandler extends AbstractCmdHandler {
    @Override
    public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        RespBody heartbeatBody = new RespBody(Command.COMMAND_HEARTBEAT_REQ).setData(new HeartbeatBody(Protocol.HEARTBEAT_BYTE));
        ImPacket heartbeatPacket = ImKit.ConvertRespPacket(heartbeatBody, channelContext);
        return heartbeatPacket;
    }

    @Override
    public Command command() {
        return Command.COMMAND_HEARTBEAT_REQ;
    }
}
