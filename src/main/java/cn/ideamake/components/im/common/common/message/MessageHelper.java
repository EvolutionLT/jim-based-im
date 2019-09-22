package cn.ideamake.components.im.common.common.message;

import cn.ideamake.components.im.common.common.listener.ImBindListener;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.pojo.dto.OperatorGroupDTO;
import cn.ideamake.components.im.pojo.vo.UserDetailVO;

import java.util.List;

/**
 * @author WChao
 * @date 2018年4月9日 下午4:31:51
 */
public interface MessageHelper {
    /**
     * 获取im群组、人员绑定监听器;
     *
     * @return
     */
    public ImBindListener getBindListener();

    /**
     * 判断用户是否在线
     *
     * @param userId
     * @return
     */
    public boolean isOnline(String userId);

    /**
     * 获取群组所有成员信息(根据type区分在线还是离线)
     *
     * @param group_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    public Group getGroupUsers(String group_id, Integer type);

    /**
     * 获取群组所有成员信息（在线+离线)
     *
     * @param user_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    public List<Group> getAllGroupUsers(String user_id, Integer type);

    /**
     * 获取好友分组所有成员信息
     *
     * @param user_id
     * @param friend_group_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    public Group getFriendUsers(String user_id, String friend_group_id, Integer type);

    /**
     * 获取好友分组所有成员信息
     *
     * @param user_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    public List<Group> getAllFriendUsers(String user_id, Integer type);

    /**
     * 根据在线类型获取用户信息;
     *
     * @param userid
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    public User getUserByType(String userid, Integer type);

    /**
     * 添加群组成员
     *
     * @param userid
     * @param group_id
     */
    public void addGroupUser(String userid, String group_id);

    /**
     * 获取群组所有成员;
     *
     * @param group_id
     * @return
     */
    public List<String> getGroupUsers(String group_id);

    /**
     * 获取用户拥有的群组;
     *
     * @param user_id
     * @return
     */
    public List<String> getGroups(String user_id);

    /**
     * 消息持久化写入
     *
     * @param timelineTable
     * @param timelineId
     * @param chatBody
     */
    public void writeMessage(String timelineTable, String timelineId, ChatBody chatBody);

    /**
     * 移除群组用户
     *
     * @param userId
     * @param group_id
     */
    public void removeGroupUser(String userId, String group_id);

    /**
     * 获取与指定用户离线消息;
     *
     * @param userId
     * @param fromUserId
     * @return
     */
    public UserMessageData getFriendsOfflineMessage(String userId, String fromUserId);

    /**
     * 获取与指定用户离线消息,不删除消息队列，用于消息的已读和未读判断;
     *
     * @param userId
     * @param fromUserId
     * @return
     */
    public UserMessageData getFriendsOfflineMessageWithoutRemove(String userId, String fromUserId);

    /**
     * 获取与所有用户离线消息;
     *
     * @param userId
     * @return
     */
    public UserMessageData getFriendsOfflineMessage(String userId);

    /**
     * 获取用户指定群组离线消息;
     *
     * @param userId
     * @param groupId
     * @return
     */
    public UserMessageData getGroupOfflineMessage(String userId, String groupId);

    /**
     * 获取与指定用户历史消息;
     *
     * @param userId
     * @param fromUserId
     * @param beginTime  消息区间开始时间
     * @param endTime    消息区间结束时间
     * @param offset     分页偏移量
     * @param count      数量
     * @return
     */
    public UserMessageData getFriendHistoryMessage(String userId, String fromUserId, Double beginTime, Double endTime, Integer offset, Integer count);

    /**
     * 获取与指定群组历史消息;
     *
     * @param userId
     * @param groupid
     * @param beginTime 消息区间开始时间
     * @param endTime   消息区间结束时间
     * @param offset    分页偏移量
     * @param count     数量
     * @return
     */
    public UserMessageData getGroupHistoryMessage(String userId, String groupid, Double beginTime, Double endTime, Integer offset, Integer count);


    //下面是对持久化的自定义接口
    public UserDetailVO getUserDetailInfo(String userId);

    public Group operateGroup(OperatorGroupDTO operatorGroupDTO);

    public Group getGroupInfo(String groupId);

    UserDetailVO initLoginUserInfo(UserReqBody userReqBody);

}
