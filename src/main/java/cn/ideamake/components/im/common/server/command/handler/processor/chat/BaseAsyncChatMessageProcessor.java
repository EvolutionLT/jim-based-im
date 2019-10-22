package cn.ideamake.components.im.common.server.command.handler.processor.chat;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.common.common.packets.ChatType;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.server.command.CommandManager;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * @author WChao
 * @date 2018年4月3日 下午1:13:32
 */
public abstract class BaseAsyncChatMessageProcessor implements AsyncChatMessageProcessor, ImConst {

	protected ImConfig imConfig = null;
	/**
	 * 供子类拿到消息进行业务处理(如:消息持久化到数据库等)的抽象方法
	 * @param chatBody
	 * @param channelContext
	 */
    protected abstract void doHandler(ChatBody chatBody, ChannelContext channelContext);

	@Override
	public boolean isProtocol(ChannelContext channelContext) {
		return true;
	}

	@Override
	public String name() {
		return BASE_ASYNC_CHAT_MESSAGE_PROCESSOR;
	}

	@Override
	public void handler(ChatBody chatBody, ChannelContext channelContext){
		if(imConfig == null) {
			imConfig = CommandManager.getImConfig();
		}
        /**
         * 20191022 区分客服置业顾问聊天渠道持久化
         */
        //进入IM业务逻辑
        if(chatBody!=null && StringUtils.isNotBlank(chatBody.getRoomId())){ return ; }
		//开启持久化
		if(ON.equals(imConfig.getIsStore())){
			//存储群聊消息;
			if(ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()){
				pushGroupMessages(PUSH,STORE, chatBody);
			}else{
				String from = chatBody.getFrom();
				String to = chatBody.getTo();
				String sessionId = ChatKit.sessionId(from,to);
				writeMessage(STORE,USER+":"+sessionId,chatBody);
				boolean isOnline = ChatKit.isOnline(to,imConfig);
				if(!isOnline){
					writeMessage(PUSH,USER+":"+to+":"+from,chatBody);
				}
			}
		}
		doHandler(chatBody, channelContext);
	}
	/**
	 * 推送持久化群组消息
	 * @param pushTable
	 * @param storeTable
	 * @param chatBody
	 */
	private void pushGroupMessages(String pushTable, String storeTable , ChatBody chatBody){
		MessageHelper messageHelper = imConfig.getMessageHelper();
		String group_id = chatBody.getGroupId();
		//先将群消息持久化到存储Timeline;
		writeMessage(storeTable,GROUP+":"+group_id,chatBody);
		List<String> userIds = messageHelper.getGroupUsers(group_id);
		//通过写扩散模式将群消息同步到所有的群成员
		for(String userId : userIds){
			boolean isOnline = false;
			if(ON.equals(imConfig.getIsStore()) && ON.equals(imConfig.getIsCluster())){
				isOnline = messageHelper.isOnline(userId);
			}else{
				isOnline = ChatKit.isOnline(userId,imConfig);
			}
			if(!isOnline){
				writeMessage(pushTable, GROUP+":"+group_id+":"+userId, chatBody);
			}
		}
	}

	private void writeMessage(String timelineTable , String timelineId , ChatBody chatBody){
		MessageHelper messageHelper = imConfig.getMessageHelper();
		messageHelper.writeMessage(timelineTable, timelineId, chatBody);
	}
}
