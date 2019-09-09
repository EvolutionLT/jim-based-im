/**
 * 
 */
package cn.ideamake.components.im.common.server.tcp;

import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.protocol.IProtocol;
import cn.ideamake.components.im.common.common.tcp.TcpPacket;
import cn.ideamake.components.im.common.common.tcp.TcpProtocol;
import cn.ideamake.components.im.common.common.tcp.TcpServerDecoder;
import cn.ideamake.components.im.common.common.tcp.TcpServerEncoder;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.handler.AbstractProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;

/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年8月3日 下午7:44:48
 */
@Slf4j
public class TcpProtocolHandler extends AbstractProtocolHandler {
	

	@Override
	public void init(ImConfig imConfig) {
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext,ChannelContext channelContext) {
		TcpPacket tcpPacket = (TcpPacket)packet;
		return TcpServerEncoder.encode(tcpPacket, groupContext, channelContext);
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext)throws Exception {
		TcpPacket tcpPacket = (TcpPacket)packet;
		AbstractCmdHandler cmdHandler = CommandManager.getCommand(tcpPacket.getCommand());
		if(cmdHandler == null){
			ImPacket imPacket = new ImPacket(Command.COMMAND_UNKNOW, new RespBody(Command.COMMAND_UNKNOW, ImStatus.C10017).toByte());
			ImAio.send(channelContext, imPacket);
			return;
		}
		ImPacket response = cmdHandler.handler(tcpPacket, channelContext);
		if(response != null && tcpPacket.getSynSeq() < 1){
			ImAio.send(channelContext,response);
		}
	}

	@Override
	public TcpPacket decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException {
		TcpPacket tcpPacket = TcpServerDecoder.decode(buffer, channelContext);
		return tcpPacket;
	}

	@Override
	public IProtocol protocol() {
		return new TcpProtocol();
	}
}
