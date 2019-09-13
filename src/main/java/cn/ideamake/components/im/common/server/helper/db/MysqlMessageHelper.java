//package cn.ideamake.components.im.common.server.helper.db;
//
//import cn.ideamake.components.im.common.common.listener.ImBindListener;
//import cn.ideamake.components.im.common.common.message.MessageHelper;
//import cn.ideamake.components.im.common.common.packets.ChatBody;
//import cn.ideamake.components.im.common.common.packets.Group;
//import cn.ideamake.components.im.common.common.packets.User;
//import cn.ideamake.components.im.common.common.packets.UserMessageData;
//import cn.ideamake.components.im.pojo.vo.UserDetailVO;
//
//import java.util.List;
//
///**
// * Mysql获取持久化+同步消息助手;
// * @author WChao
// * @date 2018年4月10日 下午4:06:26
// */
//public class MysqlMessageHelper implements MessageHelper {
//
//	@Override
//	public ImBindListener getBindListener() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void addGroupUser(String userid, String group_id) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public List<String> getGroupUsers(String group_id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void writeMessage(String timelineTable, String timelineId, ChatBody chatBody) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void removeGroupUser(String userid, String group_id) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public UserMessageData getFriendsOfflineMessage(String userid, String from_userid) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserMessageData getFriendsOfflineMessageWithoutRemove(String userId, String fromUserId) {
//		return null;
//	}
//
//	@Override
//	public UserMessageData getFriendsOfflineMessage(String userid) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserMessageData getGroupOfflineMessage(String userid, String groupid) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserMessageData getFriendHistoryMessage(String userid, String from_userid, Double beginTime, Double endTime, Integer offset, Integer count) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserMessageData getGroupHistoryMessage(String userid, String groupid, Double beginTime, Double endTime, Integer offset, Integer count) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserDetailVO getUserDetailInfo(String userId) {
//		return null;
//	}
//
//	@Override
//	public boolean isOnline(String userid) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public Group getGroupUsers(String group_id, Integer type) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Group> getAllGroupUsers(String user_id, Integer type) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Group getFriendUsers(String user_id, String friend_group_id, Integer type) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Group> getAllFriendUsers(String user_id, Integer type) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public User getUserByType(String userid, Integer type) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getGroups(String user_id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//}
