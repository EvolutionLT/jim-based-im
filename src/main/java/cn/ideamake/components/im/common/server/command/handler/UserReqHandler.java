/**
 * 
 */
package cn.ideamake.components.im.common.server.command.handler;

import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.pojo.vo.UserDetailVO;
import org.apache.commons.lang3.StringUtils;
import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.common.common.utils.ImKit;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 版本: [1.0]
 * 功能说明: 获取用户信息消息命令
 * @author : WChao 创建时间: 2017年9月18日 下午4:08:47
 */
//@Service
public class UserReqHandler extends AbstractCmdHandler {

//	@Autowired
//	private MessageHelper messageHelper;

	@Override
	public Command command() {
		return Command.COMMAND_GET_USER_REQ;
	}

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		UserReqBody userReqBody = JsonKit.toBean(packet.getBody(), UserReqBody.class);
//		User user = null;
		Collection<User> users = null;
		RespBody resPacket = null;

		String userId = Objects.requireNonNull(userReqBody).getUserid();
		if (StringUtils.isEmpty(userId)) {
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C10004), channelContext);
		}
		//(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线]);
		int type = userReqBody.getType() == null ? 2 : userReqBody.getType();
		UserDetailVO userDetailVO = null;


		//离线和在线后续处理
		if (1 == type || 2 == type) {
			resPacket = new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C200);

			//1返回简单好友列表
			if(type == 1){
				users = getUserFriends(userId);
				resPacket.setData(users);
			}
			//返回详细好友列表
			if(type == 2){
				userDetailVO = imConfig.getMessageHelper().initLoginUserInfo(userReqBody);
				resPacket.setData(userDetailVO);
			}

//			user = getUserInfo(userId, type);
			//暂时不考虑在线离线用户，后续迭代增加
//			//在线用户
//			if(0 == userReqBody.getType()){
//				resPacket = new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C10005);
//			//离线用户;
//			}else if(1 == userReqBody.getType()){
//				resPacket = new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C10006);
//			//在线+离线用户;
//			}else if(2 == userReqBody.getType()){
//				resPacket = new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C10003);
//			}
		}
		if (userDetailVO == null && users == null) {
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C10004), channelContext);
		}
		return ImKit.ConvertRespPacket(resPacket, channelContext);
	}


	/**
	 * 获取用户好友列表
	 * TODO 此处后续需要抽离出去到持久化类中
	 * @param userId
	 * @return
	 */
	public Collection<User> getUserFriends(String userId){
		RMapCache<String,User> userFriendskv = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + userId + ":" + Constants.USER.FRIENDS);
		Collection<User> userFriends =  userFriendskv.values();
		return userFriends;
	}

	/**
	 * 获取用组
	 * @return
	 */
	public Collection<Group> getUserGroups(){
		return null;
	}

	  /**
     * 根据用户id获取用户在线及离线用户;
     * @param userid
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    public User getUserInfo(String userid , Integer type){
    	User user = null;
		//是否开启持久化;
    	boolean isStore = ImConst.ON.equals(imConfig.getIsStore());
		//消息持久化助手;
    	MessageHelper messageHelper = imConfig.getMessageHelper();
    	if(isStore){
    		user = messageHelper.getUserByType(userid, 2);
    		if(user == null) {
				return null;
			}
			user.setFriends(messageHelper.getAllFriendUsers(userid, type));
			user.setGroups(messageHelper.getAllGroupUsers(userid, type));
			return user;
		}else{
			user = ImAio.getUser(userid);
		   	if(user == null) {
				return null;
			}
	   		 User copyUser = ImKit.copyUserWithoutFriendsGroups(user);
			//在线用户;
	   		 if(type == 0 || type == 1){
	   			//处理好友分组在线用户相关信息;
	   			 List<Group> friends = user.getFriends();
	   			 List<Group> onlineFriends = initOnlineUserFriendsGroups(friends,type,0);
	   			 if(onlineFriends != null){
	   				 copyUser.setFriends(onlineFriends);
	   			 }
	   			 //处理群组在线用户相关信息;
	   			 List<Group> groups = user.getGroups();
	   			 List<Group> onlineGroups = initOnlineUserFriendsGroups(groups,type,1);
	   			 if(onlineGroups != null){
	   				 copyUser.setGroups(onlineGroups);
	   			 }
	   			 return copyUser;
				 //所有用户(在线+离线);
	   		 }else if(type == 2){
	   			 return user;
	   		 }
		}
	   return user;
    }
    /**
     * 处理在线用户好友及群组用户;
     * @param groups
     * @param flag(0：好友,1:群组)
     * @return
     */
    private static List<Group> initOnlineUserFriendsGroups(List<Group> groups, Integer type, Integer flag){
	   	 if(groups == null || groups.isEmpty()) {
			 return null;
		 }
	   	 //处理好友分组在线用户相关信息;
		 List<Group> onlineGroups = new ArrayList<Group>();
		 for(Group group : groups){
			 Group copyGroup = ImKit.copyGroupWithoutUsers(group);
			 List<User> users = null;
			 if(flag == 1){
				 users = ImAio.getAllUserByGroup(group.getGroupId());
			 }else if(flag == 0){
				 users = group.getUsers();
			 }
			 if(users != null && !users.isEmpty()){
				 List<User> copyUsers = new ArrayList<User>();
				 for(User userObj : users){
					 User onlineUser = ImAio.getUser(userObj.getId());
					 //在线
					 if(onlineUser != null && type == 0){
						 User copyOnlineUser = ImKit.copyUserWithoutFriendsGroups(onlineUser);
						 copyUsers.add(copyOnlineUser);
					 //离线
					 }else if(onlineUser == null && type == 1){
						 User copyOnlineUser = ImKit.copyUserWithoutFriendsGroups(onlineUser);
						 copyUsers.add(copyOnlineUser);
					 }
				 }
				 copyGroup.setUsers(copyUsers);
			 }
			 onlineGroups.add(copyGroup);
		 }
		 return onlineGroups;
    }
}
