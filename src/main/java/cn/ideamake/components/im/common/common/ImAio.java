package cn.ideamake.components.im.common.common;

import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.RespBody;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.server.helper.redis.RedisMessageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.cluster.ImCluster;
import cn.ideamake.components.im.common.common.config.DefaultImConfigBuilder;
import cn.ideamake.components.im.common.common.listener.ImBindListener;
import cn.ideamake.components.im.common.common.packets.Client;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.common.common.utils.ImKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelContextFilter;
import org.tio.core.GroupContext;
import org.tio.utils.lock.SetWithLock;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

/**
 * 版本: [1.0]
 * 功能说明:
 *
 * @author : WChao 创建时间: 2017年9月22日 上午9:07:18
 */
public class ImAio {

    public static ImConfig imConfig = null;

    private static Logger log = LoggerFactory.getLogger(cn.ideamake.components.im.common.common.ImAio.class);

    private static String CHAT_BODY = "{ \"to\":\"%s\", \"from\":\"%s\", \"cmd\":11, \"msgType\":0, \"chatType\":2, \"extras\": { \"nickName\" : \"%s\", \"headImg\":\"%s\", \"isAutoMessage\":\"%s\" }, \"content\":\"%s\" }";

    /**
     * 功能描述：[根据用户ID获取当前用户]
     *
     * @param userId
     * @return
     * @author：WChao 创建时间: 2017年9月18日 下午4:34:39
     */
    public static User getUser(String userId) {
        SetWithLock<ChannelContext> userChannelContexts = cn.ideamake.components.im.common.common.ImAio.getChannelContextsByUserId(userId);
        if (Objects.isNull(userChannelContexts)) {
            return null;
        }
        ReadLock readLock = userChannelContexts.getLock().readLock();
        readLock.lock();
        try {
            Set<ChannelContext> userChannels = userChannelContexts.getObj();
            if (CollectionUtils.isEmpty(userChannels)) {
                return null;
            }
            User user = null;
            for (ChannelContext channelContext : userChannels) {
                ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
                Client client = imSessionContext.getClient();
                user = client.getUser();
                if (user != null) {
                    return user;
                }
            }
            return user;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 功能描述：[根据用户ID获取当前用户所在通道集合]
     *
     * @param userId
     * @return
     * @author：WChao 创建时间: 2017年9月18日 下午4:34:39
     */
    public static SetWithLock<ChannelContext> getChannelContextsByUserId(String userId) {
        SetWithLock<ChannelContext> channelContexts = Aio.getChannelContextsByUserid(imConfig.getGroupContext(), userId);
        return channelContexts;
    }

    /**
     * 功能描述：[获取所有用户(在线+离线)]
     *
     * @return
     * @author：WChao 创建时间: 2017年9月18日 下午4:31:54
     */
    public static List<User> getAllUser() {
        List<User> users = new ArrayList<User>();
        SetWithLock<ChannelContext> allChannels = Aio.getAllChannelContexts(imConfig.getGroupContext());
        if (allChannels == null) {
            return users;
        }
        ReadLock readLock = allChannels.getLock().readLock();
        readLock.lock();
        try {
            Set<ChannelContext> userChannels = allChannels.getObj();
            if (CollectionUtils.isEmpty(userChannels)) {
                return users;
            }
            for (ChannelContext channelContext : userChannels) {
                ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
                Client client = imSessionContext.getClient();
                if (client != null && client.getUser() != null) {
                    User user = ImKit.copyUserWithoutUsers(client.getUser());
                    users.add(user);
                }
            }
        } finally {
            readLock.unlock();
        }
        return users;
    }

    /**
     * 功能描述：[获取所有在线用户]
     *
     * @return
     * @author：WChao 创建时间: 2017年9月18日 下午4:31:42
     */
    public static List<User> getAllOnlineUser() {
        List<User> users = new ArrayList<User>();
        SetWithLock<ChannelContext> onlineChannels = Aio.getAllConnectedsChannelContexts(imConfig.getGroupContext());
        if (onlineChannels == null) {
            return users;
        }
        ReadLock readLock = onlineChannels.getLock().readLock();
        readLock.lock();
        try {
            Set<ChannelContext> userChannels = onlineChannels.getObj();
            for (ChannelContext channelContext : userChannels) {
                ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
                if (imSessionContext != null) {
                    Client client = imSessionContext.getClient();
                    if (client != null && client.getUser() != null) {
                        User user = ImKit.copyUserWithoutUsers(client.getUser());
                        users.add(user);
                    }
                }
            }
        } finally {
            readLock.unlock();
        }
        return users;
    }

    /**
     * 根据群组获取所有用户;
     *
     * @param group
     * @return
     */
    public static List<User> getAllUserByGroup(String group) {
        SetWithLock<ChannelContext> withLockChannels = Aio.getChannelContextsByGroup(imConfig.getGroupContext(), group);
        if (withLockChannels == null) {
            return null;
        }
        ReadLock readLock = withLockChannels.getLock().readLock();
        readLock.lock();
        try {
            Set<ChannelContext> channels = withLockChannels.getObj();
            if (CollectionUtils.isEmpty(channels)) {
                return null;
            }
            List<User> users = new ArrayList<User>();
            Map<String, User> userMaps = new HashMap<String, User>();
            for (ChannelContext channelContext : channels) {
                String userId = channelContext.getUserid();
                User user = getUser(userId);
                if (user != null && userMaps.get(userId) == null) {
                    userMaps.put(userId, user);
                    users.add(user);
                }
            }
            userMaps = null;
            return users;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 功能描述：[发送到群组(所有不同协议端)]
     *
     * @param group
     * @param packet
     * @author：WChao 创建时间: 2017年9月21日 下午3:26:57
     */
    public static void sendToGroup(String group, ImPacket packet) {
        if (packet.getBody() == null) {
            return;
        }
        SetWithLock<ChannelContext> withLockChannels = Aio.getChannelContextsByGroup(imConfig.getGroupContext(), group);
        if (withLockChannels == null) {
            ImCluster cluster = imConfig.getCluster();
            if (cluster != null && !packet.isFromCluster()) {
                cluster.clusterToGroup(imConfig.getGroupContext(), group, packet);
            }
            return;
        }
        ReadLock readLock = withLockChannels.getLock().readLock();
        readLock.lock();
        try {
            Set<ChannelContext> channels = withLockChannels.getObj();
            if (CollectionUtils.isNotEmpty(channels)) {
                for (ChannelContext channelContext : channels) {
                    send(channelContext, packet);
                }
            }
        } finally {
            readLock.unlock();
            ImCluster cluster = imConfig.getCluster();
            if (cluster != null && !packet.isFromCluster()) {
                cluster.clusterToGroup(imConfig.getGroupContext(), group, packet);
            }
        }
    }

    /**
     * 发送到指定通道;
     *
     * @param channelContext
     * @param packet
     */
    public static boolean send(ChannelContext channelContext, ImPacket packet) {
        ImPacket respPacket = initAndSetConvertPacket(channelContext, packet);
        if (respPacket == null) {
            return false;
        }
        return Aio.send(channelContext, respPacket);
    }

    /**
     * 阻塞发送（确认把packet发送到对端后再返回）
     *
     * @param channelContext
     * @param packet
     * @return
     */
    public static boolean bSend(ChannelContext channelContext, ImPacket packet) {
        ImPacket respPacket = initAndSetConvertPacket(channelContext, packet);
        if (respPacket == null) {
            return false;
        }
        return Aio.bSend(channelContext, respPacket);
    }

    /**
     * @description: 给指定人发送指定消息
     * @param: [user, receiverId, message]
     * @return: void
     * @author: apollo
     * @date: 2019-10-11
     */
    public static void sendToUser(User user, String receiverId, String message, String isAutoMessage) {
        if (Objects.isNull(user) || StringUtils.isBlank(receiverId)) {
            return;
        }
        String body = String.format(CHAT_BODY, receiverId, user.getId(), user.getNick(), user.getAvatar(), isAutoMessage, message);
        ImPacket chatPacket = new ImPacket(Command.COMMAND_CHAT_REQ, new RespBody(Command.COMMAND_CHAT_REQ, ChatKit.toChatBody(body.getBytes())).toByte());
        ImAio.sendToUser(receiverId, chatPacket);

    }

    /**
     * 发送到指定用户;
     *
     * @param userId
     * @param packet
     */
    public static void sendToUser(String userId, ImPacket packet) {
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        SetWithLock<ChannelContext> toChannelContexts = getChannelContextsByUserId(userId);
        if (toChannelContexts == null || toChannelContexts.size() < 1) {
            ImCluster cluster = imConfig.getCluster();
            if (cluster != null && !packet.isFromCluster()) {
                cluster.clusterToUser(imConfig.getGroupContext(), userId, packet);
            }
            return;
        }
        ReadLock readLock = toChannelContexts.getLock().readLock();
        readLock.lock();
        try {
            Set<ChannelContext> channels = toChannelContexts.getObj();
            for (ChannelContext channelContext : channels) {
                send(channelContext, packet);
            }
        } finally {
            readLock.unlock();
            ImCluster cluster = imConfig.getCluster();
            if (cluster != null && !packet.isFromCluster()) {
                cluster.clusterToUser(imConfig.getGroupContext(), userId, packet);
            }
        }
    }

    /**
     * 发送到指定ip对应的集合
     *
     * @param groupContext
     * @param ip
     * @param packet
     */
    public static void sendToIp(GroupContext groupContext, String ip, ImPacket packet) {
        sendToIp(groupContext, ip, packet, null);
    }

    public static void sendToIp(GroupContext groupContext, String ip, ImPacket packet, ChannelContextFilter channelContextFilter) {
        sendToIp(groupContext, ip, packet, channelContextFilter, false);
    }

    private static Boolean sendToIp(GroupContext groupContext, String ip, ImPacket packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
        try {
            SetWithLock<ChannelContext> setWithLock = groupContext.ips.clients(groupContext, ip);
            if (setWithLock == null) {
                log.info("{}, 没有ip为[{}]的对端", groupContext.getName(), ip);
                return false;
            } else {
                Boolean ret = sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
                return ret;
            }
        } finally {
            ImCluster cluster = imConfig.getCluster();
            if (cluster != null && !packet.isFromCluster()) {
                cluster.clusterToIp(groupContext, ip, packet);
            }
        }
    }

    public static Boolean sendToSet(GroupContext groupContext, SetWithLock<ChannelContext> setWithLock, ImPacket packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
        Lock lock = setWithLock.getLock().readLock();
        lock.lock();
        try {
            Set<ChannelContext> sets = (Set<ChannelContext>) setWithLock.getObj();
            for (ChannelContext channelContext : sets) {
                SetWithLock<ChannelContext> convertSet = new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());
                convertSet.add(channelContext);
                ImPacket resPacket = ImKit.ConvertRespPacket(packet, packet.getCommand(), channelContext);
                Aio.sendToSet(groupContext, convertSet, resPacket, channelContextFilter);
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * 转换协议包同时设置Packet包信息;
     *
     * @param channelContext
     * @param packet
     * @return
     */
    private static ImPacket initAndSetConvertPacket(ChannelContext channelContext, ImPacket packet) {
        if (channelContext == null) {
            return null;
        }
        ImPacket respPacket = ImKit.ConvertRespPacket(packet, packet.getCommand(), channelContext);
        if (respPacket == null) {
            log.error("转换协议包为空,请检查协议！");
            return null;
        }
        respPacket.setSynSeq(packet.getSynSeq());
        if (imConfig == null) {
            imConfig = new DefaultImConfigBuilder().setGroupContext(channelContext.getGroupContext()).build();
        }
        return respPacket;
    }

    /**
     * 绑定用户;
     *
     * @param channelContext
     * @param userId
     */
    public static void bindUser(ChannelContext channelContext, String userId) {
        bindUser(channelContext, userId, null);
    }

    /**
     * 绑定用户,同时可传递监听器执行回调函数
     *
     * @param channelContext
     * @param userId
     * @param bindListener(绑定监听器回调)
     */
    public static void bindUser(ChannelContext channelContext, String userId, ImBindListener bindListener) {
        Aio.bindUser(channelContext, userId);
        if (bindListener != null) {
            try {
                bindListener.onAfterUserBind(channelContext, userId);
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    /**
     * 解绑用户
     *
     * @param userId
     */
    public static void unbindUser(String userId) {
        unbindUser(userId, null);
    }

    /**
     * 解除绑定用户,同时可传递监听器执行回调函数
     *
     * @param userId
     * @param bindListener(解绑定监听器回调)
     */
    public static void unbindUser(String userId, ImBindListener bindListener) {
        Aio.unbindUser(imConfig.getGroupContext(), userId);
        if (bindListener != null) {
            try {
                SetWithLock<ChannelContext> userChannelContexts = cn.ideamake.components.im.common.common.ImAio.getChannelContextsByUserId(userId);
                if (userChannelContexts == null || userChannelContexts.size() == 0) {
                    return;
                }
                ReadLock readLock = userChannelContexts.getLock().readLock();
                readLock.lock();
                try {
                    Set<ChannelContext> channels = userChannelContexts.getObj();
                    for (ChannelContext channelContext : channels) {
                        bindListener.onAfterUserBind(channelContext, userId);
                    }
                } finally {
                    readLock.unlock();
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    /**
     * 绑定群组;
     *
     * @param channelContext
     * @param group
     */
    public static void bindGroup(ChannelContext channelContext, String group) {
        bindGroup(channelContext, group, null);
    }

    /**
     * 绑定群组,同时可传递监听器执行回调函数
     *
     * @param channelContext
     * @param group
     * @param bindListener(绑定监听器回调)
     */
    public static void bindGroup(ChannelContext channelContext, String group, ImBindListener bindListener) {
        Aio.bindGroup(channelContext, group);
        if (bindListener != null) {
            try {
                bindListener.onAfterGroupBind(channelContext, group);
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    /**
     * 与指定群组解除绑定
     *
     * @param userId
     * @param group
     */
    public static void unbindGroup(String userId, String group) {
        unbindGroup(userId, group, null);
    }

    /**
     * 与指定群组解除绑定,同时可传递监听器执行回调函数
     *
     * @param userId
     * @param group
     * @param bindListener(解绑定监听器回调)
     */
    public static void unbindGroup(String userId, String group, ImBindListener bindListener) {
        SetWithLock<ChannelContext> userChannelContexts = cn.ideamake.components.im.common.common.ImAio.getChannelContextsByUserId(userId);
        if (userChannelContexts == null || userChannelContexts.size() == 0) {
            return;
        }
        ReadLock readLock = userChannelContexts.getLock().readLock();
        readLock.lock();
        try {
            Set<ChannelContext> channels = userChannelContexts.getObj();
            for (ChannelContext channelContext : channels) {
                Aio.unbindGroup(group, channelContext);
                if (bindListener != null) {
                    try {
                        bindListener.onAfterGroupUnbind(channelContext, group);
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                    }
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 移除用户, 和close方法一样，只不过不再进行重连等维护性的操作
     *
     * @param userId
     * @param remark
     */
    public static void remove(String userId, String remark) {
        SetWithLock<ChannelContext> userChannelContexts = getChannelContextsByUserId(userId);
        if (userChannelContexts != null && userChannelContexts.size() > 0) {
            ReadLock readLock = userChannelContexts.getLock().readLock();
            readLock.lock();
            try {
                Set<ChannelContext> channels = userChannelContexts.getObj();
                for (ChannelContext channelContext : channels) {
                    remove(channelContext, remark);
                }
            } finally {
                readLock.unlock();
            }
        }
    }

    /**
     * 移除指定channel, 和close方法一样，只不过不再进行重连等维护性的操作
     *
     * @param channelContext
     * @param remark
     */
    public static void remove(ChannelContext channelContext, String remark) {
        Aio.remove(channelContext, remark);
    }
}
