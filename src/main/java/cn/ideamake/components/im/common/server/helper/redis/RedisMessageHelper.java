package cn.ideamake.components.im.common.server.helper.redis;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.cache.redis.JedisTemplate;
import cn.ideamake.components.im.common.common.cache.redis.RedisCache;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.listener.ImBindListener;
import cn.ideamake.components.im.common.common.message.AbstractMessageHelper;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.common.enums.RestEnum;
import cn.ideamake.components.im.common.exception.IMException;
import cn.ideamake.components.im.pojo.constant.VankeRedisKey;
import cn.ideamake.components.im.pojo.dto.OperatorGroupDTO;
import cn.ideamake.components.im.pojo.vo.UserDetailVO;
import cn.ideamake.components.im.pojo.vo.UserFriendsVO;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RMapCache;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Redis获取持久化+同步消息助手;
 *
 * @author WChao
 * @date 2018年4月9日 下午4:39:30
 */
//@SuppressWarnings("unchecked")
@Service
@Slf4j
public class RedisMessageHelper extends AbstractMessageHelper {

    private static RedisCache groupCache = null;
    private static RedisCache pushCache = null;
    private static RedisCache storeCache = null;
    private static RedisCache userCache = null;
    // 两天时间内为最近联系人
    private final static long LASTED_CONTACT_TIME = 2 * 24 * 60 * 60 * 1000L;

    private final String SUBFIX = ":";
//	private Logger log = LoggerFactory.getLogger(cn.ideamake.components.im.common.server.helper.redis.RedisMessageHelper.class);

    static {
        RedisCacheManager.register(USER, Integer.MAX_VALUE, Integer.MAX_VALUE);
        RedisCacheManager.register(GROUP, Integer.MAX_VALUE, Integer.MAX_VALUE);
        RedisCacheManager.register(STORE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        RedisCacheManager.register(PUSH, Integer.MAX_VALUE, Integer.MAX_VALUE);
        groupCache = RedisCacheManager.getCache(GROUP);
        pushCache = RedisCacheManager.getCache(PUSH);
        storeCache = RedisCacheManager.getCache(STORE);
        userCache = RedisCacheManager.getCache(USER);
    }

    public RedisMessageHelper() {
    }

    //初始化时需要设置imConfig配置
    public void init(ImConfig imConfig) {
    }
//	public RedisMessageHelper(ImConfig imConfig){
//		this.groupCache = RedisCacheManager.getCache(GROUP);
//		this.pushCache = RedisCacheManager.getCache(PUSH);
//		this.storeCache = RedisCacheManager.getCache(STORE);
//		this.userCache = RedisCacheManager.getCache(USER);
//		this.imConfig = imConfig;
//	}

    @Override
    public ImBindListener getBindListener() {

        return new RedisImBindListener(imConfig);
    }

    @Override
    public boolean isOnline(String userid) {
        try {
            Set<String> keys = JedisTemplate.me().keys(USER + SUBFIX + userid + SUBFIX + TERMINAL);
            if (keys != null && keys.size() > 0) {
                Iterator<String> keyitr = keys.iterator();
                while (keyitr.hasNext()) {
                    String key = keyitr.next();
                    key = key.substring(key.indexOf(userid));
                    String isOnline = userCache.get(key, String.class);
                    if (ONLINE.equals(isOnline)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }

    @Override
    public List<String> getGroupUsers(String group_id) {
        String group_user_key = group_id + SUBFIX + USER;
        List<String> users = groupCache.listGetAll(group_user_key);
        return users;
    }


    @Override
    public void writeMessage(String timelineTable, String timelineId, ChatBody chatBody) {
        double score = chatBody.getCreateTime();
        RedisCacheManager.getCache(timelineTable).sortSetPush(timelineId, score, chatBody);
    }


    @Override
    public void addGroupUser(String userid, String groupId) {
        //此处后续添加判断组是否存在#TODO
        List<String> users = groupCache.listGetAll(groupId + SUBFIX + USER);
        if (!users.contains(userid)) {
//			groupCache.listPushTail(groupId, userid);
            groupCache.listPushTail(groupId + SUBFIX + USER, userid);
        }
    }

    @Override
    @Transactional
    public void removeGroupUser(String userId, String groupId) {
        groupCache.listRemove(groupId, userId);
        //删除用户拥有的组内容
        userCache.listRemove(userId + SUBFIX + GROUP, groupId);

    }

    @Override
    public UserMessageData getFriendsOfflineMessage(String userid, String from_userid) {
        String key = USER + SUBFIX + userid + SUBFIX + from_userid;
        List<String> messageList = pushCache.sortSetGetAll(key);
        List<ChatBody> datas = JsonKit.toArray(messageList, ChatBody.class);
        pushCache.remove(key);
        return putFriendsMessage(new UserMessageData(userid), datas);
    }

    @Override
    public UserMessageData getFriendsOfflineMessageWithoutRemove(String userId, String fromUserId) {
        String key = USER + SUBFIX + userId + SUBFIX + fromUserId;
        List<String> messageList = pushCache.sortSetGetAll(key);
        List<ChatBody> datas = JsonKit.toArray(messageList, ChatBody.class);
        return putFriendsMessage(new UserMessageData(userId), datas);
    }

    @Override
    @Transactional
    public UserMessageData getFriendsOfflineMessage(String userid) {
        try {
            Set<String> keys = JedisTemplate.me().keys(PUSH + SUBFIX + USER + SUBFIX + userid);
            UserMessageData messageData = new UserMessageData(userid);
            if (keys != null && keys.size() > 0) {
                List<ChatBody> results = new ArrayList<ChatBody>();
                Iterator<String> keyitr = keys.iterator();
                //获取好友离线消息;后续改成key+
                while (keyitr.hasNext()) {
                    String key = keyitr.next();
                    key = key.substring(key.indexOf(USER + SUBFIX));
                    List<String> messages = pushCache.sortSetGetAll(key);
                    pushCache.remove(key);
                    results.addAll(JsonKit.toArray(messages, ChatBody.class));
                }
                putFriendsMessage(messageData, results);
            }
            List<String> groups = userCache.listGetAll(userid + SUBFIX + GROUP);
            //获取群组离线消息;
            if (groups != null) {
                for (String groupid : groups) {
                    UserMessageData groupMessageData = getGroupOfflineMessage(userid, groupid);
                    if (groupMessageData != null) {
                        putGroupMessage(messageData, groupMessageData.getGroups().get(groupid));
                    }
                }
            }
            return messageData;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    @Override
    public UserMessageData getGroupOfflineMessage(String userid, String groupid) {
        String key = GROUP + SUBFIX + groupid + SUBFIX + userid;
        List<String> messages = pushCache.sortSetGetAll(key);
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }
        UserMessageData messageData = new UserMessageData(userid);
        putGroupMessage(messageData, JsonKit.toArray(messages, ChatBody.class));
        pushCache.remove(key);
        return messageData;
    }

    @Override
    public UserMessageData getFriendHistoryMessage(String userid, String from_userid, Double beginTime, Double endTime, Integer offset, Integer count) {
        String sessionId = ChatKit.sessionId(userid, from_userid);
        List<String> messages = null;
        String key = USER + SUBFIX + sessionId;
        boolean isTimeBetween = (beginTime != null && endTime != null);
        boolean isPage = (offset != null && count != null);
        //消息区间，不分页
        if (isTimeBetween && !isPage) {
            messages = storeCache.sortSetGetAll(key, beginTime, endTime);
            //消息区间，并且分页;
        } else if (isTimeBetween && isPage) {
            messages = storeCache.sortSetGetAll(key, beginTime, endTime, offset, count);
            //所有消息，并且分页;
        } else if (!isTimeBetween && isPage) {
            messages = storeCache.sortSetGetAll(key, 0, Double.MAX_VALUE, offset, count);
            //所有消息，不分页;
        } else {
            messages = storeCache.sortSetGetAll(key);
        }
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }
        UserMessageData messageData = new UserMessageData(userid);
        putFriendsHistoryMessage(messageData, JsonKit.toArray(messages, ChatBody.class), from_userid);
        return messageData;
    }

    @Override
    public UserMessageData getGroupHistoryMessage(String userid, String groupid, Double beginTime, Double endTime, Integer offset, Integer count) {
        String key = GROUP + SUBFIX + groupid;
        List<String> messages = null;
        boolean isTimeBetween = (beginTime != null && endTime != null);
        boolean isPage = (offset != null && count != null);
        //消息区间，不分页
        if (isTimeBetween && !isPage) {
            messages = storeCache.sortSetGetAll(key, beginTime, endTime);
            //消息区间，并且分页;
        } else if (isTimeBetween && isPage) {
            messages = storeCache.sortSetGetAll(key, beginTime, endTime, offset, count);
            //所有消息，并且分页;
        } else if (!isTimeBetween && isPage) {
            messages = storeCache.sortSetGetAll(key, 0, Double.MAX_VALUE, offset, count);
            //所有消息，不分页;
        } else {
            messages = storeCache.sortSetGetAll(key);
        }
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }
        UserMessageData messageData = new UserMessageData(userid);
        putGroupMessage(messageData, JsonKit.toArray(messages, ChatBody.class));
        return messageData;
    }


    /**
     * 放入用户群组消息;
     *
     * @param userMessage
     * @param messages
     */
    public UserMessageData putGroupMessage(UserMessageData userMessage, List<ChatBody> messages) {
        if (userMessage == null || messages == null) {
            return null;
        }
        for (ChatBody chatBody : messages) {
            String group = chatBody.getGroupId();
            if (StringUtils.isEmpty(group)) {
                continue;
            }
            List<ChatBody> groupMessages = userMessage.getGroups().get(group);
            if (groupMessages == null) {
                groupMessages = new ArrayList<ChatBody>();
                userMessage.getGroups().put(group, groupMessages);
            }
            groupMessages.add(chatBody);
        }
        return userMessage;
    }

    /**
     * 放入用户好友消息;
     *
     * @param userMessage
     * @param messages
     */
    public UserMessageData putFriendsMessage(UserMessageData userMessage, List<ChatBody> messages) {
        if (userMessage == null || messages == null) {
            return null;
        }
        for (ChatBody chatBody : messages) {
            String fromUserId = chatBody.getFrom();
            if (StringUtils.isEmpty(fromUserId)) {
                continue;
            }
            List<ChatBody> friendMessages = userMessage.getFriends().get(fromUserId);
            if (friendMessages == null) {
                friendMessages = new ArrayList<ChatBody>();
                userMessage.getFriends().put(fromUserId, friendMessages);
            }
            friendMessages.add(chatBody);
        }
        return userMessage;
    }

    /**
     * 放入用户好友历史消息;
     *
     * @param userMessage
     * @param messages
     */
    public UserMessageData putFriendsHistoryMessage(UserMessageData userMessage, List<ChatBody> messages, String friendId) {
        if (userMessage == null || messages == null) {
            return null;
        }
        for (ChatBody chatBody : messages) {
            String fromUserId = chatBody.getFrom();
            if (StringUtils.isEmpty(fromUserId)) {
                continue;
            }
            List<ChatBody> friendMessages = userMessage.getFriends().get(friendId);
            if (friendMessages == null) {
                friendMessages = new ArrayList<ChatBody>();
                userMessage.getFriends().put(friendId, friendMessages);
            }
            friendMessages.add(chatBody);
        }
        return userMessage;
    }

    /**
     * 获取群组所有成员信息
     *
     * @param group_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    @Override
    public Group getGroupUsers(String group_id, Integer type) {
        if (group_id == null || type == null) {
            return null;
        }
        Group group = groupCache.get(group_id + SUBFIX + INFO, Group.class);
        if (group == null) {
            return null;
        }
        List<String> userIds = this.getGroupUsers(group_id);
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        List<User> users = new ArrayList<User>();
        for (String userId : userIds) {
            User user = getUserByType(userId, type);
            if (user != null) {
                String status = user.getStatus();
                if (type == 0 && ONLINE.equals(status)) {
                    users.add(user);
                } else if (type == 1 && OFFLINE.equals(status)) {
                    users.add(user);
                } else if (type == 2) {
                    users.add(user);
                }
            }
        }
        group.setUsers(users);
        return group;
    }

    /**
     * 根据在线类型获取用户信息;
     *
     * @param userid
     * @param type
     * @return
     */
    @Override
    public User getUserByType(String userid, Integer type) {
        User user = userCache.get(userid + SUBFIX + INFO, User.class);
        if (user == null) {
            return null;
        }
        boolean isOnline = this.isOnline(userid);
        String status = isOnline ? ONLINE : OFFLINE;
        if (type == 0 || type == 1) {
            if (type == 0 && isOnline) {
                user.setStatus(status);
            } else if (type == 1 && !isOnline) {
                user.setStatus(status);
            }
        } else if (type == 2) {
            user.setStatus(status);
        }
        return user;
    }

    /**
     * 获取好友分组所有成员信息
     *
     * @param user_id
     * @param friend_group_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */

    @Override
    public Group getFriendUsers(String user_id, String friend_group_id, Integer type) {
        if (user_id == null || friend_group_id == null || type == null) {
            return null;
        }
        List<Group> friends = userCache.get(user_id + SUBFIX + FRIENDS, List.class);
        if (friends == null || friends.isEmpty()) {
            return null;
        }
        for (Group group : friends) {
            if (friend_group_id.equals(group.getGroupId())) {
                List<User> users = group.getUsers();
                if (CollectionUtils.isEmpty(users)) {
                    return null;
                }
                List<User> userResults = new ArrayList<User>();
                for (User user : users) {
                    initUserStatus(user);
                    String status = user.getStatus();
                    if (type == 0 && ONLINE.equals(status)) {
                        userResults.add(user);
                    } else if (type == 1 && OFFLINE.equals(status)) {
                        userResults.add(user);
                    } else {
                        userResults.add(user);
                    }
                }
                group.setUsers(userResults);
                return group;
            }
        }
        return null;
    }

    /**
     * 初始化用户在线状态;
     *
     * @param user
     */
    public void initUserStatus(User user) {
        if (user == null) {
            return;
        }
        String userId = user.getId();
        boolean isOnline = this.isOnline(userId);
        if (isOnline) {
            user.setStatus(ONLINE);
        } else {
            user.setStatus(OFFLINE);
        }
    }

    /**
     * 获取好友分组所有成员信息
     *
     * @param user_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    @Override
    public List<Group> getAllFriendUsers(String user_id, Integer type) {
        if (user_id == null) {
            return null;
        }
        //应对后续对用户信息的更新，避免遍历所有用户做更新，使用RMapCache<String,User>存储
        List<JSONObject> friendJsonArray = userCache.get(user_id + SUBFIX + FRIENDS, List.class);
        if (CollectionUtils.isEmpty(friendJsonArray)) {
            return null;
        }
        List<Group> friends = new ArrayList<Group>();
        for (JSONObject groupJson : friendJsonArray) {
            Group group = JSONObject.toJavaObject(groupJson, Group.class);
            List<User> users = group.getUsers();
            if (CollectionUtils.isEmpty(users)) {
                continue;
            }
            List<User> userResults = new ArrayList<User>();
            for (User user : users) {
                initUserStatus(user);
                String status = user.getStatus();
                if (type == 0 && ONLINE.equals(status)) {
                    userResults.add(user);
                } else if (type == 1 && OFFLINE.equals(status)) {
                    userResults.add(user);
                } else if (type == 2) {
                    userResults.add(user);
                }
            }
            group.setUsers(userResults);
            friends.add(group);
        }
        return friends;
    }

    /**
     * 获取群组所有成员信息（在线+离线)
     *
     * @param user_id
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    @Override
    public List<Group> getAllGroupUsers(String user_id, Integer type) {
        if (user_id == null) {
            return null;
        }
        List<String> group_ids = userCache.listGetAll(user_id + SUBFIX + GROUP);
        if (CollectionUtils.isEmpty(group_ids)) {
            return null;
        }
        List<Group> groups = new ArrayList<Group>();
        for (String group_id : group_ids) {
            Group group = getGroupUsers(group_id, type);
            if (group != null) {
                groups.add(group);
            }
        }
        return groups;
    }

    /**
     * 获取用户拥有的群组;
     *
     * @param user_id
     * @return
     */
    @Override
    public List<String> getGroups(String user_id) {
        List<String> groups = userCache.listGetAll(user_id + SUBFIX + GROUP);
        return groups;
    }

    //此处一下是根据实际业务需求新增的适配接口

    @Override
    public UserDetailVO getUserDetailInfo(String userId) {
        return null;
    }

    @Override
    @Transactional
    public Group operateGroup(OperatorGroupDTO operatorGroupDTO) {
        String ownerId = checkToken(operatorGroupDTO.getToken());
        switch (operatorGroupDTO.getOperatorId()) {
            case ADD:
                //组id暂时用时间戳
                String groupId = String.valueOf(System.nanoTime());
                String groupName = operatorGroupDTO.getGroup().getName() == null ? "默认群名" : operatorGroupDTO.getGroup().getName();
                Group group = new Group(ownerId, groupId, groupName);
                group.setAvatar(operatorGroupDTO.getGroup().getAvatar());
                log.info("用户{},添加组{}", ownerId, group);
                //将组添加到持久化存储里
                groupCache.put(groupId + SUBFIX + INFO, group);
                //将组添加到用户的拥有组中
                groupCache.listPushTail(groupId + SUBFIX + USER, ownerId);
                //将用户添加到自己的组列表中
                userCache.listPushTail(ownerId + SUBFIX + GROUP, groupId);
                return group;
            case GET:
                String groupIdGet = operatorGroupDTO.getGroupId();
                Group searchGroup = groupCache.get(groupIdGet + SUBFIX + INFO, Group.class);
                return searchGroup;
            case DELETE:
                Group groupDelete = groupCache.get(operatorGroupDTO.getGroupId() + SUBFIX + INFO, Group.class);
                if (groupDelete == null) {
                    log.warn("组[ {} ]不存在，请检查数据", operatorGroupDTO.getGroupId());
                    //此处检查系统剩余脏数据清理，清除自身在组里信息
                    checkReomovedGroup(operatorGroupDTO.getGroupId());
                    return operatorGroupDTO.getGroup();
                }
                if (groupDelete.getOwnerId() != null && groupDelete.getOwnerId().equals(ownerId)) {
                    groupCache.remove(operatorGroupDTO.getGroupId() + SUBFIX + INFO);
                    log.info("组[ {} ]删除成功", groupDelete.getGroupId());
                    checkReomovedGroup(operatorGroupDTO.getGroupId());
                    return operatorGroupDTO.getGroup();
                } else {
                    throw new IMException("操作越权");
                }
            case UPDATE:
                log.info("更新组信息");
                Group groupUpdate = groupCache.get(operatorGroupDTO.getGroupId() + SUBFIX + INFO, Group.class);
                if (groupUpdate == null) {
                    log.warn("组[{}]不存在，请检查数据", operatorGroupDTO.getGroup());
                    return operatorGroupDTO.getGroup();
                }
                if (groupUpdate.getOwnerId() != null && groupUpdate.getOwnerId().equals(ownerId)) {
                    Group groupDto = operatorGroupDTO.getGroup();
                    BeanUtils.copyProperties(groupDto, groupUpdate);
                    groupCache.put(operatorGroupDTO.getGroupId() + SUBFIX + INFO, groupDto);
                } else {
                    throw new IMException("操作越权");
                }
                return operatorGroupDTO.getGroup();
        }
        return null;
    }

    @Override
    public Group getGroupInfo(String groupId) {
        return groupCache.get(groupId + SUBFIX + INFO, Group.class);
    }


    /**
     * 此接口暂时用于快速开发，用户量不大时处理,冗余万科相关业务
     *
     * @param userId
     * @return
     */
    @Override
    public UserDetailVO initLoginUserInfo(UserReqBody userReqBody) {
        long now = System.currentTimeMillis() / 1000;
        String userId = userReqBody.getUserid();
        JSONObject extras = userReqBody.getExtras();
        if (MapUtils.isEmpty(extras) || Objects.isNull(extras.get("pullType"))) {
            return null;
        }
        //是否是待回复好友
        boolean isReplyFriend = false;
        //是否是最近联系人
        boolean isLastedFriend = false;
        //是否需要搜索
        Object searchKey = extras.get("searchKey");
        boolean isSearch = searchKey == null || StringUtils.isBlank(searchKey.toString());
        // 1=全部联系人 2=待回复联系人 3=最近联系人
        Integer pullType = NumberUtils.toInt(extras.get("pullType").toString(), 1);
        UserDetailVO userDetailVO = new UserDetailVO();
        //获取登录人的基本信息
        User user = userCache.get(userId + SUBFIX + INFO, User.class);
        if (user == null) {
            throw new IMException(RestEnum.USER_NOT_FOUND);
        }

		//初始化用户基本信息
		userDetailVO.setNickname(user.getNick()==null?"":user.getNick());
		userDetailVO.setUserId(user.getId());
		userDetailVO.setAvatar(user.getAvatar()==null?"":user.getAvatar());

        //初始化用户群组信息
        RMapCache<String, User> friendsIds = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + userId + ":" + Constants.USER.FRIENDS);
        if (MapUtils.isEmpty(friendsIds)) {
            return userDetailVO;
        }
        //统计待回复信息数量
        int pendingReplyNum = 0;
        //最近联系人
        int lastedContactsNum = 0;
        List<UserFriendsVO> friendsVOS = new ArrayList<>();
        RedisCache cache = RedisCacheManager.getCache(ImConst.USER);
        String pendingReplyNumKey = String.format(VankeRedisKey.VANKE_CHAT_PENDING_REPLY_NUM_KEY, userId);
        RMapCache<String, Long> mapCache = RedissonTemplate.me().getRedissonClient().getMapCache(pendingReplyNumKey);
        //次数先做遍历初始化，默认用户不会特别多，后续再优化内容结构
        for (Map.Entry<String, User> entry : friendsIds.entrySet()) {
            String friendId = entry.getKey();
            User friend = entry.getValue();
            //次数存储取第一次存储的用户数据，后续实时更新可存id，拉取时遍历获取
            UserFriendsVO userFriendsVO = new UserFriendsVO();
            //每次取最新的，后续将好友存在改成id
            User userFriend = userCache.get(friendId + SUBFIX + INFO, User.class);
            String nickName = userFriend.getNick();
            //根据搜索关键字过滤
            if (!isSearch && StringUtils.isNotEmpty(nickName) && !nickName.equalsIgnoreCase(searchKey.toString().trim())) {
                continue;
            }
            userFriendsVO.setNickname(nickName);
            userFriendsVO.setUserId(friendId);
            userFriendsVO.setAvatar(userFriend.getAvatar() == null ? "" : userFriend.getAvatar());
            String sessionId = ChatKit.sessionId(userId, friendId);
            String key = USER + SUBFIX + sessionId;
            //取10条聊天纪录
//            List<String> messages = storeCache.sortSetGetAll(key, 0, Double.MAX_VALUE, 0, 10);
            List<String> messages = storeCache.sortReSetGetAll(key, 0, Double.MAX_VALUE, 0, 10);
            if (!messages.isEmpty()) {
                List<ChatBody> chatBodyList = JsonKit.toArray(messages, ChatBody.class);
                userFriendsVO.setHistoryMessage(chatBodyList);
                //最后一条聊天记录的时间
                long contactTime = chatBodyList.get(chatBodyList.size() - 1).getCreateTime().longValue();
                if (now - contactTime <= LASTED_CONTACT_TIME) {
                    lastedContactsNum++;
                    isLastedFriend = true;
                }
            } else {
                userFriendsVO.setHistoryMessage(Collections.emptyList());
            }
            //在线未读消息
            int onlineUnReadNum = Optional.ofNullable(cache.get(String.format(VankeRedisKey.VANKE_CHAT_UNREAD_NUM_KEY, userId, friendId), Long.class)).orElse(0L).intValue();
            String keyPushUnread = USER + SUBFIX + userId + SUBFIX + friendId;
            //统计离线未读信息
            List<String> messageList = pushCache.sortSetGetAll(keyPushUnread);
            if (messageList.isEmpty()) {
                userFriendsVO.setUnReadNum(onlineUnReadNum);
            } else {
                //判断离线未读消息是否已经统计在待回复消息里面
                if (!mapCache.containsKey(friendId)) {
                    mapCache.put(friendId, 1L);
                }
                isReplyFriend = true;
                //在线未读消息+离线未读消息
                userFriendsVO.setUnReadNum(onlineUnReadNum + messageList.size());

            }
            //统计根据关键字搜索时，待回复消息数量
            if (mapCache.containsKey(friendId)) {
                pendingReplyNum++;
            }

            //拉取待回复列表
            if (pullType == 2) {
                if (isReplyFriend) {
                    friendsVOS.add(userFriendsVO);
                }
                continue;
            } else if (pullType == 3) {
                //拉取最近联系人
                if (isLastedFriend) {
                    friendsVOS.add(userFriendsVO);
                }
                continue;
            }
            //拉取全部
            friendsVOS.add(userFriendsVO);
        }
        if (pullType == 1) {
            //重置最近联系人
            String lastedContactNum = String.format(VankeRedisKey.VANKE_CHAT_LASTED_CONTACT_SNUM_KEY, userId);
            cache.put(lastedContactNum, lastedContactsNum);
        }
        userDetailVO.setFriends(friendsVOS);
        userDetailVO.setPendingReplyNum(isSearch ? mapCache.size() : pendingReplyNum);
        userDetailVO.setLastedContactsNum(lastedContactsNum);
        userDetailVO.setAllContactsNum(isSearch ? friendsIds.size() : friendsVOS.size());
        userDetailVO.setPullType(pullType);
        return userDetailVO;
    }

    /**
     * 检查授权token
     * 成功返回用户id
     *
     * @param token
     * @return
     */
    public static String checkToken(String token) {
        RMapCache<String, User> mapCache = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.ITEM_LABEL.PERIOD + "::" + Constants.PEROID.USER_TOKEN);
        User user = mapCache.get(token);
        if (user != null) {
            return user.getId();
        }
        throw new IMException(RestEnum.TOKEN_EXPIRED);
    }

    private void checkReomovedGroup(String groupId) {
        //此处处理清除工作，有三处地方
        //1.拥有组的成员组信息；2.组的聊天纪录看情况清除
        //此处多线程操作可能会存在问题,实际操作时会存在管理员把群删除了，用户还是可以看到组信息，暂时先删除，后续看具体逻辑
        //群主删除群后，用户也看不到群聊
        List<String> groupUserIds = groupCache.listGetAll(groupId + SUBFIX + USER);
        if (!groupUserIds.isEmpty()) {
            groupUserIds.forEach(userId -> {
                userCache.listRemove(userId + SUBFIX + GROUP, groupId);

                //群主解散时清除群成员相关的组信息
                List<String> userGroups = userCache.listGetAll(userId + SUBFIX + GROUP);
                if (userGroups.isEmpty()) {
                    userCache.remove((userId + SUBFIX + GROUP));
                }
            });
            //为空时删除对应key值list
//			if(groupUserIds.isEmpty()){
            groupCache.remove(groupId + SUBFIX + USER);
//			}
        }
    }

}


