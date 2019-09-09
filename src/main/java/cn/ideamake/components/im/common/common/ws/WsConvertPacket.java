/**
 * 
 */
package cn.ideamake.components.im.common.common.ws;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.protocol.IConvertProtocolPacket;
import cn.ideamake.components.im.common.common.ws.Opcode;
import cn.ideamake.components.im.common.common.ws.WsResponsePacket;
import cn.ideamake.components.im.common.common.ws.WsSessionContext;
import org.tio.core.ChannelContext;

/**
 * Ws协议消息转化包
 * @author WChao
 *
 */
public class WsConvertPacket implements IConvertProtocolPacket {

	/**
	 * WebSocket响应包;
	 */
	@Override
	public ImPacket RespPacket(byte[] body, Command command, ChannelContext channelContext) {
		Object sessionContext = channelContext.getAttribute();
		//转ws协议响应包;
		if(sessionContext instanceof WsSessionContext){
			WsResponsePacket wsResponsePacket = new WsResponsePacket();
			wsResponsePacket.setBody(body);
			wsResponsePacket.setWsOpcode(Opcode.TEXT);
			wsResponsePacket.setCommand(command);
			return wsResponsePacket;
		}
		return null;
	}

	@Override
	public ImPacket ReqPacket(byte[] body, Command command, ChannelContext channelContext) {
		
		return null;
	}
}
