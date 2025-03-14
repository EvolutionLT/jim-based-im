/**
 * 
 */
package cn.ideamake.components.im.common.common.packets;

import com.alibaba.fastjson.JSONObject;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月26日 上午11:34:44
 */
public class ChatBody extends Message {

	private static final long serialVersionUID = 5731474214655476286L;
	/**
	 * 发送用户id;
	 */
	private String from;
	/**
	 * 目标用户id;
	 */
	private String to;
	/**
	 * 消息类型;(如：0:text、1:image、2:voice、3:vedio、4:music、5:news)
	 */
	private Integer msgType;
	/**
	 * 聊天类型;(如公聊、私聊)
	 */
	private Integer chatType;
	/**
	 * 消息内容;
	 */
	private String content;
	/**
	 * 消息发到哪个群组;
	 */
	private String groupId;
	private String roomId;
	private String other;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	private ChatBody(){}

	private ChatBody(String id , String from , String to , Integer msgType , Integer chatType , String content , String groupId,String roomId, String other, Integer cmd , Long createTime , JSONObject extras){
		this.id = id;
		this.from = from;
		this.to = to;
		this.msgType = msgType;
		this.chatType = chatType;
		this.content = content;
		this.groupId = groupId;
		this.roomId = roomId;
		this.other = other;
		this.cmd = cmd;
		this.createTime = createTime;
		this.extras = extras;
	}

	public static ChatBody.Builder newBuilder(){
		return new ChatBody.Builder();
	}
	public String getFrom() {
		return from;
	}
	public ChatBody setFrom(String from) {
		this.from = from;
		return this;
	}
	public String getTo() {
		return to;
	}
	public ChatBody setTo(String to) {
		this.to = to;
		return this;
	}

	public Integer getMsgType() {
		return msgType;
	}
	public ChatBody setMsgType(Integer msgType) {
		this.msgType = msgType;
		return this;
	}
	public String getContent() {
		return content;
	}
	public ChatBody setContent(String content) {
		this.content = content;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}
	public ChatBody setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}
	public Integer getChatType() {
		return chatType;
	}
	public ChatBody setChatType(Integer chatType) {
		this.chatType = chatType;
		return this;
	}

	public static class Builder extends Message.Builder<ChatBody, ChatBody.Builder>{
		/**
		 * 来自user_id;
		 */
		private String from;
		/**
		 * 目标user_id;
		 */
		private String to;
		/**
		 * 消息类型;(如：0:text、1:image、2:voice、3:vedio、4:music、5:news)
		 */
		private Integer msgType;
		/**
		 * 聊天类型;(如公聊、私聊)
		 */
		private Integer chatType;
		/**
		 * 消息内容;
		 */
		private String content;
		/**
		 * 消息发到哪个群组;
		 */
		private String groupId;
		/**
		 * 房间ID
		 */
		private String roomId;
		/**
		 * 其他字段
		 */
		private String other;
		
		public Builder(){};
		
		public Builder setFrom(String from) {
			this.from = from;
			return this;
		}
		public Builder setTo(String to) {
			this.to = to;
			return this;
		}
		public Builder setMsgType(Integer msgType) {
			this.msgType = msgType;
			return this;
		}
		public Builder setChatType(Integer chatType) {
			this.chatType = chatType;
			return this;
		}
		public Builder setContent(String content) {
			this.content = content;
			return this;
		}
		public Builder setGroupId(String groupId) {
			this.groupId = groupId;
			return this;
		}
		@Override
		protected Builder getThis() {
			return this;
		}
		@Override
		public ChatBody build(){
			return new ChatBody(this.id , this.from , this.to , this.msgType , this.chatType , this.content , this.groupId, this.roomId, this.other, this.cmd , this.createTime , this.extras);
		}
	}
}
