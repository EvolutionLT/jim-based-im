/**
 * 
 */
package cn.ideamake.components.im.common.server.http.api;

import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.http.HttpConfig;
import cn.ideamake.components.im.common.common.http.HttpRequest;
import cn.ideamake.components.im.common.common.http.HttpResponse;
import cn.ideamake.components.im.common.common.packets.CloseReqBody;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.command.handler.ChatReqHandler;
import cn.ideamake.components.im.common.server.command.handler.CloseReqHandler;
import cn.ideamake.components.im.common.server.http.annotation.RequestPath;
import cn.ideamake.components.im.common.server.util.HttpResps;
import org.tio.core.ChannelContext;

/**
 * 版本: [1.0]
 * 功能说明: Http协议消息发送控制器类
 * @author : WChao 创建时间: 2017年8月8日 上午9:08:48
 */
@RequestPath(value = "/api")
public class HttpApiController {
	
	@RequestPath(value = "/message/send")
	public HttpResponse chat(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		HttpResponse response = new HttpResponse(request,httpConfig);
		ChatReqHandler chatReqHandler = CommandManager.getCommand(Command.COMMAND_CHAT_REQ, ChatReqHandler.class);
		ImPacket chatPacket = chatReqHandler.handler(request, channelContext);
		if(chatPacket != null){
			response = (HttpResponse)chatPacket;
		}
		return response;
	}
	/**
	 * 判断用户是否在线接口;
	 * @param request
	 * @param httpConfig
	 * @param channelContext
	 * @return
	 * @throws Exception
	 */
	@RequestPath(value = "/user/online")
	public HttpResponse online(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		Object[] params = request.getParams().get("userid");
		if(params == null || params.length == 0){
			return HttpResps.json(request, new RespBody(ImStatus.C10020));
		}
		String userId = params[0].toString();
		User user = ImAio.getUser(userId);
		if(user != null){
			return HttpResps.json(request, new RespBody(ImStatus.C10019));
		}else{
			return HttpResps.json(request, new RespBody(ImStatus.C10001));
		}
	}
	/**
	 * 关闭指定用户;
	 * @param request
	 * @param httpConfig
	 * @param channelContext
	 * @return
	 * @throws Exception
	 */
	@RequestPath(value = "user/close")
	public HttpResponse close(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		Object[] params = request.getParams().get("userid");
		if(params == null || params.length == 0){
			return HttpResps.json(request, new RespBody(ImStatus.C10020));
		}
		String userId = params[0].toString();
		ImPacket closePacket = new ImPacket(Command.COMMAND_CLOSE_REQ,new CloseReqBody(userId).toByte());
		CloseReqHandler closeReqHandler = CommandManager.getCommand(Command.COMMAND_CLOSE_REQ, CloseReqHandler.class);
		closeReqHandler.handler(closePacket, channelContext);
		return HttpResps.json(request, new RespBody(ImStatus.C10021));
	}
}
