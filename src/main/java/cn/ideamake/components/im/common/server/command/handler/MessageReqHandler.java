package cn.ideamake.components.im.common.server.command.handler;

import org.apache.commons.lang3.StringUtils;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.MessageReqBody;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.packets.UserMessageData;
import cn.ideamake.components.im.common.common.utils.ImKit;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import org.tio.core.ChannelContext;

/**
 * 获取聊天消息命令处理器
 * @author WChao
 * @date 2018年4月10日 下午2:40:07
 */
public class MessageReqHandler extends AbstractCmdHandler {
	
	@Override
	public Command command() {
		
		return Command.COMMAND_GET_MESSAGE_REQ;
	}

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		RespBody resPacket = null;
		MessageReqBody messageReqBody = null;
		try{
			messageReqBody = JsonKit.toBean(packet.getBody(), MessageReqBody.class);
		}catch (Exception e) {
			//用户消息格式不正确
			return getMessageFailedPacket(channelContext);
		}
		UserMessageData messageData = null;
		MessageHelper messageHelper = imConfig.getMessageHelper();
		//群组ID;
		String groupId = messageReqBody.getGroupId();
		//当前用户ID;
		String userId = messageReqBody.getUserId();
		//消息来源用户ID;
		String fromUserId = messageReqBody.getFromUserId();
		//消息区间开始时间;
		Double beginTime = messageReqBody.getBeginTime();
		//消息区间结束时间;
		Double endTime = messageReqBody.getEndTime();
		//分页偏移量;
		Integer offset = messageReqBody.getOffset();
		//分页数量;
		Integer count = messageReqBody.getCount();
		//消息类型;
		int type = messageReqBody.getType();
		//如果用户ID为空或者type格式不正确，获取消息失败;
		if(StringUtils.isEmpty(userId) || (0 != type && 1 != type && 2 != type) || !ImConst.ON.equals(imConfig.getIsStore())){
			return getMessageFailedPacket(channelContext);
		}
		if(type == 0 ){
			resPacket = new RespBody(Command.COMMAND_GET_MESSAGE_RESP, ImStatus.C10016);
		}else if(type == 1){
			resPacket = new RespBody(Command.COMMAND_GET_MESSAGE_RESP, ImStatus.C10018);
		}else if(type ==2) {
			resPacket = new RespBody(Command.COMMAND_GET_MESSAGE_RESP, ImStatus.C10022);
		}
		//群组ID不为空获取用户该群组消息;
		if(!StringUtils.isEmpty(groupId)){
			//离线消息;
			if(0 == type){
				messageData = messageHelper.getGroupOfflineMessage(userId,groupId);
			//历史消息;
			}else if(1 == type){
				messageData = messageHelper.getGroupHistoryMessage(userId, groupId,beginTime,endTime,offset,count);
			}
		}else if(StringUtils.isEmpty(fromUserId)){
			//获取用户所有离线消息(好友+群组);
			if(0 == type){
				messageData = messageHelper.getFriendsOfflineMessage(userId);
			}else{
				return getMessageFailedPacket(channelContext);
			}
		}else{
			//获取与指定用户离线消息,并删除推送队列;
			if(0 == type){
				messageData = messageHelper.getFriendsOfflineMessage(userId, fromUserId);
			//获取与指定用户历史消息;
			}else if(1 == type){
				messageData = messageHelper.getFriendHistoryMessage(userId, fromUserId,beginTime,endTime,offset,count);
			}else if(2 == type){
				messageData = messageHelper.getFriendsOfflineMessageWithoutRemove(userId, fromUserId);
			}
		}
		resPacket.setData(messageData);
		return ImKit.ConvertRespPacket(resPacket, channelContext);
	}
	/**
	 * 获取用户消息失败响应包;
	 * @param channelContext
	 * @return
	 */
	public ImPacket getMessageFailedPacket(ChannelContext channelContext){
		RespBody resPacket = new RespBody(Command.COMMAND_GET_MESSAGE_RESP, ImStatus.C10015);
		return ImKit.ConvertRespPacket(resPacket, channelContext);
	}
}