/**
 * 
 */
package cn.ideamake.components.im.common.common.utils;

import cn.ideamake.components.im.common.common.id.impl.UUIDSessionIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import cn.ideamake.components.im.common.common.*;
import cn.ideamake.components.im.common.common.config.Config;
import cn.ideamake.components.im.common.common.http.HttpConst;
import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.packets.User;
import org.tio.core.ChannelContext;
import org.tio.utils.lock.SetWithLock;

import java.util.Optional;

/**
 * IM聊天命令工具类
 * @date 2018-09-05 23:29:30
 * @author WChao
 *
 */
@Slf4j
public class ChatKit {
	

	/**
	 * 转换为聊天消息结构;
	 * @param body
	 * @param channelContext
	 * @return
	 */
	public static ChatBody toChatBody(byte[] body, ChannelContext channelContext){
		ChatBody chatReqBody = parseChatBody(body);
		return getChatBody(channelContext, chatReqBody);
	}

	/**
	 * 转换为聊天消息结构;
	 * @param body
	 * @return
	 */
	public static ChatBody toChatBody(byte[] body){
		return parseChatBody(body);
	}

	private static ChatBody getChatBody(ChannelContext channelContext, ChatBody chatReqBody) {
		if (chatReqBody != null) {
			if (StringUtils.isEmpty(chatReqBody.getFrom())) {
				ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
				User user = imSessionContext.getClient().getUser();
				if (user != null) {
					chatReqBody.setFrom(user.getNick());
				} else {
					chatReqBody.setFrom(channelContext.getId());
				}
			}
		}
		return chatReqBody;
	}

	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param body
	 * @return
	 */
	private static ChatBody parseChatBody(byte[] body){
		if(body == null) {
			return null;
		}
		ChatBody chatReqBody = null;
		try{
			String text = new String(body, HttpConst.CHARSET_NAME);
		    chatReqBody = JsonKit.toBean(text, ChatBody.class);
			if(chatReqBody != null){
				if(chatReqBody.getCreateTime() == null) {
					chatReqBody.setCreateTime(System.currentTimeMillis());
				}
				if(StringUtils.isEmpty(chatReqBody.getId())){
					chatReqBody.setId(UUIDSessionIdGenerator.instance.sessionId(null));
				}
				return chatReqBody;
			}
		}catch(Exception e){
			log.error(e.toString());
		}
		return chatReqBody;
	}

	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param bodyStr
	 * @return
	 */
	public static ChatBody parseChatBody(String bodyStr){
		if(bodyStr == null) {
			return null;
		}
		ChatBody chatReqBody = JsonKit.toBean(bodyStr, ChatBody.class);
		return Optional.ofNullable(chatReqBody).map(e -> {
			e.setCreateTime(System.currentTimeMillis());
			if(StringUtils.isEmpty(chatReqBody.getId())){
				chatReqBody.setId(UUIDSessionIdGenerator.instance.sessionId(null));
			}
			return e;
		}).orElse(chatReqBody);
	}

    /**
	  * 聊天数据格式不正确响应包
	  * @param channelContext
	  * @return imPacket
	  * @throws Exception
    */
   public static ImPacket dataInCorrectRespPacket(ChannelContext channelContext) throws Exception{
	   RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP, ImStatus.C10002);
	   ImPacket respPacket = ImKit.ConvertRespPacket(chatDataInCorrectRespPacket, channelContext);
	   respPacket.setStatus(ImStatus.C10002);
	   return respPacket;
   }

   /**
    * 聊天发送成功响应包
    * @param channelContext
    * @return imPacket
    * @throws Exception
    */
    public static ImPacket sendSuccessRespPacket(ChannelContext channelContext) throws Exception{
 	   RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP, ImStatus.C10000);
 	   ImPacket respPacket = ImKit.ConvertRespPacket(chatDataInCorrectRespPacket, channelContext);
 	   respPacket.setStatus(ImStatus.C10000);
 	   return respPacket;
    }

    /**
     * 聊天用户不在线响应包
     * @param channelContext
     * @return
     * @throws Exception
     */
     public static ImPacket offlineRespPacket(ChannelContext channelContext) throws Exception{
  	   RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP, ImStatus.C10001);
  	   ImPacket respPacket = ImKit.ConvertRespPacket(chatDataInCorrectRespPacket, channelContext);
  	   respPacket.setStatus(ImStatus.C10001);
  	   return respPacket;
     }

     /**
      * 判断用户是否在线
      * @param userId
	  * @param imConfig
      * @return
      */
     public static boolean isOnline(String userId , Config imConfig){
    	 boolean isStore = ImConst.ON.equals(imConfig.getIsStore());
		 if(isStore){
			return imConfig.getMessageHelper().isOnline(userId);
		 }
    	 SetWithLock<ChannelContext> toChannelContexts = ImAio.getChannelContextsByUserId(userId);
    	 if(toChannelContexts != null && toChannelContexts.size() > 0){
    		 return true;
    	 }
    	 return false;
     }

	/**
	 * 获取双方会话ID
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public static String sessionId(String from, String to) {
		if (from.compareTo(to) <= 0) {
			return from + "-" + to;
		} else {
			return to + "-" + from;
		}
	}
}
