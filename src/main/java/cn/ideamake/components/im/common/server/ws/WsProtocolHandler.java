/**
 * 
 */
package cn.ideamake.components.im.common.server.ws;

import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.http.HttpRequest;
import cn.ideamake.components.im.common.common.http.HttpRequestDecoder;
import cn.ideamake.components.im.common.common.http.HttpResponse;
import cn.ideamake.components.im.common.common.http.HttpResponseEncoder;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.Message;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.protocol.IProtocol;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.common.ws.*;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.handler.AbstractProtocolHandler;
import cn.ideamake.components.im.common.server.ws.WsMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午6:38:36
 */
public class WsProtocolHandler extends AbstractProtocolHandler {
	
	private Logger logger = LoggerFactory.getLogger(cn.ideamake.components.im.common.server.ws.WsProtocolHandler.class);
	
	private WsServerConfig wsServerConfig;

	private IWsMsgHandler wsMsgHandler;
	
	public WsProtocolHandler() {}
	
	public WsProtocolHandler(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler) {
		this.wsServerConfig = wsServerConfig;
		this.wsMsgHandler = wsMsgHandler;
	}
	@Override
	public void init(ImConfig imConfig) {
		WsServerConfig wsServerConfig = imConfig.getWsServerConfig();
		if(wsServerConfig == null){
			wsServerConfig = new WsServerConfig();
			imConfig.setWsServerConfig(wsServerConfig);
		}
		IWsMsgHandler wsMsgHandler = wsServerConfig.getWsMsgHandler();
		if(wsMsgHandler == null){
			wsServerConfig.setWsMsgHandler(new WsMsgHandler());
		}
		this.wsServerConfig = wsServerConfig;
		this.wsMsgHandler = wsServerConfig.getWsMsgHandler();
		logger.info("wsServerHandler 初始化完毕...");
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		WsSessionContext wsSessionContext = (WsSessionContext)channelContext.getAttribute();
		WsResponsePacket wsResponsePacket = (WsResponsePacket)packet;
		if (wsResponsePacket.getCommand() == Command.COMMAND_HANDSHAKE_RESP) {
			//握手包
			HttpResponse handshakeResponsePacket = wsSessionContext.getHandshakeResponsePacket();
			return HttpResponseEncoder.encode(handshakeResponsePacket, groupContext, channelContext,true);
		}else{
			return WsServerEncoder.encode(wsResponsePacket , groupContext, channelContext);
		}
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		WsRequestPacket wsRequestPacket = (WsRequestPacket) packet;
		AbstractCmdHandler cmdHandler = CommandManager.getCommand(wsRequestPacket.getCommand());
		if(cmdHandler == null){
			//是否ws分片发包尾帧包
			if(!wsRequestPacket.isWsEof()) {
				return;
			}
			ImPacket imPacket = new ImPacket(Command.COMMAND_UNKNOW, new RespBody(Command.COMMAND_UNKNOW, ImStatus.C10017).toByte());
			ImAio.send(channelContext, imPacket);
			return;
		}
		ImPacket response = cmdHandler.handler(wsRequestPacket, channelContext);
		if(response != null){
			ImAio.send(channelContext, response);
		}
	}

	@Override
	public ImPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		WsSessionContext wsSessionContext = (WsSessionContext)channelContext.getAttribute();
		//握手
		if(!wsSessionContext.isHandshaked()){
			HttpRequest httpRequest = HttpRequestDecoder.decode(buffer,channelContext,true);
			if(httpRequest == null) {
				return null;
			}
			//升级到WebSocket协议处理
			HttpResponse httpResponse = WsServerDecoder.updateWebSocketProtocol(httpRequest,channelContext);
			if (httpResponse == null) {
				throw new AioDecodeException("http协议升级到websocket协议失败");
			}
			wsSessionContext.setHandshakeRequestPacket(httpRequest);
			wsSessionContext.setHandshakeResponsePacket(httpResponse);

			WsRequestPacket wsRequestPacket = new WsRequestPacket();
			wsRequestPacket.setHandShake(true);
			wsRequestPacket.setCommand(Command.COMMAND_HANDSHAKE_REQ);
			return wsRequestPacket;
		}else{
			WsRequestPacket wsRequestPacket = WsServerDecoder.decode(buffer, channelContext);
			if(wsRequestPacket == null) {
				return null;
			}
			Command command = null;
			if(wsRequestPacket.getWsOpcode() == Opcode.CLOSE){
				command = Command.COMMAND_CLOSE_REQ;
			}else{
				try{
					Message message = JsonKit.toBean(wsRequestPacket.getBody(), Message.class);
					command = Command.forNumber(message.getCmd());
				}catch(Exception e){
					return wsRequestPacket;
				}
			}
			wsRequestPacket.setCommand(command);
			return wsRequestPacket;
		}
	}
	public WsServerConfig getWsServerConfig() {
		return wsServerConfig;
	}

	public void setWsServerConfig(WsServerConfig wsServerConfig) {
		this.wsServerConfig = wsServerConfig;
	}

	public IWsMsgHandler getWsMsgHandler() {
		return wsMsgHandler;
	}

	public void setWsMsgHandler(IWsMsgHandler wsMsgHandler) {
		this.wsMsgHandler = wsMsgHandler;
	}

	@Override
	public IProtocol protocol() {
		return new WsProtocol();
	}
}
