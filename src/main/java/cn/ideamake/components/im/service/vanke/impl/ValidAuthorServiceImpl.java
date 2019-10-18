package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.common.exception.BusinessException;
import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.common.enums.RestEnum;
import cn.ideamake.components.im.common.exception.IMException;
import cn.ideamake.components.im.common.server.helper.redis.RedisMessageHelper;
import cn.ideamake.components.im.dto.mapper.*;
import cn.ideamake.components.im.pojo.constant.UserType;
import cn.ideamake.components.im.pojo.constant.VankeChatStaus;
import cn.ideamake.components.im.pojo.constant.VankeRedisKey;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.pojo.entity.CusChatMember;
import cn.ideamake.components.im.pojo.entity.IMUsers;
import cn.ideamake.components.im.service.vanke.AysnChatService;
import cn.ideamake.components.im.service.vanke.IMUserService;
import cn.ideamake.components.im.service.vanke.ValidAuthorService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.utils.lock.SetWithLock;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @program jio-based-im
 * @description: 万科登录service
 * @author: apollo
 * @create: 2019/09/17 15:33
 */
@Service
@Slf4j
public class ValidAuthorServiceImpl implements ValidAuthorService {
    /**
     * IM客服新需求
     *
     * @param dto
     */
    @Resource
    private IMUserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private VisitorMapper visitorMapper;

    @Resource
    private CusChatMemberMapper cusChatMemberMapper;

    @Resource
    private CusInfoMapper cusInfoMapper;

    @Resource
    private UserVisitorMapper userVisitorMapper;

    @Resource
    private CusVisitorMapper cusVisitorMapper;

    @Resource
    private AysnChatService aysnChatService;

    @Resource
    private IMUserMapper imUserMapper;

//    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50));

//    private static final int EXPIRE_TIME = 7 * 24 * 60;

    private static final long lOCK_EXPIRE_TIME = 2 * 60L;

    private static String CHAT_BODY = "{ \"to\":\"%s\", \"from\":\"%s\", \"cmd\":11, \"msgType\":0, \"chatType\":2, \"extras\": { \"nickName\" : \"%s\", \"headImg\":\"%s\" }, \"content\":\"%s\" }";


    @Override
    public void initUserInfo(VankeLoginDTO dto) {
        @NotBlank String senderId = dto.getSenderId();
        @NotBlank String token = dto.getToken();
        @NotNull Integer type = dto.getType();
        valid(senderId, token, type);
        User user = RedisCacheManager.getCache(ImConst.USER).get(senderId + ":" + Constants.USER.INFO, User.class);
        //每次登陆更新用户信息，可以及时更新用户的微信昵称和头像变动
        cacheUserInfo(dto);
        //初始化数据
        aysnChatService.synInitChatMember(dto);
    }


    private void valid(@NotBlank String senderId, @NotBlank String token, @NotNull Integer type) {
        Boolean isValid = false;
        //查询db，判断数据的准确性
        if (type.equals(UserType.CUSTOMER.getType())) {
            isValid = Optional.ofNullable(cusInfoMapper.userIsValid(senderId, token)).orElse(Boolean.FALSE);
        }

        if (type.equals(UserType.VISITOR.getType())) {
            isValid = Optional.ofNullable(visitorMapper.userIsValid(senderId, token)).orElse(Boolean.FALSE);
        }

        if (type.equals(UserType.ESTATE_AGENT.getType())) {
            isValid = Optional.ofNullable(userMapper.userIsValid(senderId, token)).orElse(Boolean.FALSE);
        }

        if (!isValid) {
            throw new IllegalArgumentException("VankeLoginService-validToken(), token is error, token:" + token);
        }
    }

    @Override
    public User getReceiverInfo(VankeLoginDTO dto) {
        @NotBlank String senderId = dto.getSenderId();
        String receiverId = dto.getReceiverId();
        @NotBlank String token = dto.getToken();
        @NotNull Integer type = dto.getType();
        valid(senderId, token, type);
        //校验发送人合法性
        //每次登陆更新用户信息，可以及时更新用户的微信昵称和头像变动
        User user = cacheUserInfo(dto);
        //校验接收人合法性
        if (StringUtils.isNotBlank(receiverId)) {
            return cacheFriend(dto);
        }
        String lockKey = String.format(VankeRedisKey.VANKE_CHAT_LOGIN_LOCK_KEY, senderId);
        RLock lock = RedissonTemplate.me().getRedissonClient().getLock(lockKey);
        try {
            boolean tryLock = lock.tryLock(lOCK_EXPIRE_TIME, TimeUnit.SECONDS);
            if (!tryLock) {
                throw new BusinessException(500, "正在努力帮您联系客服，请耐心等待!");
            }
            //查询接收人信息
            return getReceiver(dto, user);
        } catch (InterruptedException e) {
            log.error("ValidAuthorService-getReceiverInfo(), try lock is error, error: ", e);
            return null;
        } finally {
            lock.forceUnlock();
        }
    }

    private User getReceiver(VankeLoginDTO dto, User user) {
        @NotBlank String senderId = dto.getSenderId();
        //访客类型,需要匹配聊天对象
        //判断有没有绑定置业顾问
        String projectCode = dto.getProjectCode();
        String userId;
        if (StringUtils.isNotBlank(projectCode) && StringUtils.isNotBlank(userId = userVisitorMapper.selectUserId(senderId, projectCode))) {
            dto.setReceiverId(userId);
            return cacheFriend(dto);
        }
        //判断有没有绑定客服, 没有绑定则匹配空闲客服
        return Optional.ofNullable(cusVisitorMapper.selectCusInfoByVisitor(senderId)).map(e -> {
            dto.setReceiverId(e.getUuId());
            return cacheFriend(dto);
        }).orElse(getRandomCustomer(dto, user));
    }

    /**
     * @description: 给客服推送消息“您的专属客服目前不在线，可给客服留言”
     * @param: [user, receiverId, message]
     * @return: cn.ideamake.components.im.common.common.packets.User
     * @author: apollo
     * @date: 2019-10-08
     */
    private void sendMessage(User user, String receiverId, String message) {
        String userId = user.getId();
        if (!new RedisMessageHelper().isOnline(userId)) {
            String body = String.format(CHAT_BODY, receiverId, userId, user.getNick(), user.getAvatar(), message);
            ImPacket chatPacket = new ImPacket(Command.COMMAND_CHAT_REQ, new RespBody(Command.COMMAND_CHAT_REQ, ChatKit.toChatBody(body.getBytes())).toByte());
            ImAio.sendToUser(receiverId, chatPacket);
        }
    }

    private User getRandomCustomer(VankeLoginDTO dto, User user) {
        List<CusChatMember> members = cusChatMemberMapper.selectCustomer();
        if (CollectionUtils.isEmpty(members)) {
            throw new IllegalArgumentException("当前客服繁忙，请耐心等待!");
        }
        int random = RandomUtils.nextInt(0, members.size());
        String to = members.get(random).getUserId();
        dto.setReceiverId(to);
//        RedisCacheManager.getCache(ImConst.USER).incr(String.format(VankeRedisKey.VANKE_CHAT_MEMBER_NUM_KEY, to), 1);
//        User friend = cacheFriend(dto);
//        CompletableFuture.runAsync(() -> {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                //TODO
//            }
////            ImAio.sendToUser(friend, user.getId(), "您好! 我是万科置业客服，有什么可以帮您!", "false");
//            ImAio.sendToUser(user, friend.getId(), "您好, 我想咨询下楼盘信息！", "false");
//        }, threadPoolExecutor);
        return cacheFriend(dto);
    }

    private User cacheFriend(VankeLoginDTO dto) {
        @NotBlank String senderId = dto.getSenderId();
        @NotBlank String to = dto.getReceiverId();
        User friend = Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(to + ":" + Constants.USER.INFO, User.class)).orElseThrow(() -> new IMException(RestEnum.USER_NOT_FOUND));
        log.info("cacheFriend, sender: {}, receiver: {}, friend: {}", senderId, to, JSON.toJSON(friend));
        //快速出基本功能，使用jim原先好友存储List<User>，后续改成RMapCache<String,User>形式，便于做消息更新；
        RMapCache<String, User> friendsOfSender = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + senderId + ":" + Constants.USER.FRIENDS);
        //发送者好友列表没有接收者时，将接收者添加到其好友列表
        if (friendsOfSender.isEmpty() || !friendsOfSender.containsKey(to)) {
            log.info("friendsOfSender, sender: {}, receiver: {}, friend: {}", senderId, to, JSON.toJSON(friend));
            friendsOfSender.put(to, friend);
        }
        RMapCache<String, User> friendsOfReceiver = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + to + ":" + Constants.USER.FRIENDS);
        //同样检查接收者好友列表，若没有发送时，将接收者添加到其好友列表
        if (friendsOfReceiver.isEmpty() || !friendsOfReceiver.containsKey(senderId)) {
            User user = Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(senderId + ":" + Constants.USER.INFO, User.class)).orElseThrow(() -> new IMException(RestEnum.USER_NOT_FOUND));
            log.info("friendsOfReceiver, sender: {}, receiver: {}, user: {}", senderId, to, JSON.toJSON(user));
            friendsOfReceiver.put(senderId, user);
        }
        dto.setReceiverId(friend.getId());
        aysnChatService.synInitChatInfo(dto);
        return friend;
    }

    @Override
    public LoginRespBody doLogin(LoginReqBody loginReqBody, ChannelContext channelContext) {
        String logStr = "VankeLoginService-doLogin(), ";
        log.info(logStr + "input: {}", JSON.toJSONString(loginReqBody));
        User user;
        if (StringUtils.isNotBlank(loginReqBody.getToken())) {
            String token = loginReqBody.getToken();
            if (StringUtils.isBlank(token)) {
                log.info(logStr + "token is null!");
                return null;
            }
            user = RedisCacheManager.getCache(ImConst.USER).get(token + ":" + Constants.USER.INFO, User.class);
        } else {
            user = getUserInfo(loginReqBody.getUserId(), loginReqBody);
        }
        log.info(logStr + "result: {}", JSON.toJSONString(user));
        return Optional.ofNullable(user).map(e -> new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10007, e)).orElse(new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10008));
    }

    //TODO 登录成功回调方法
    @Override
    public void onSuccess(ChannelContext channelContext) {
        aysnChatService.synUpdateMember(channelContext.getUserid(), VankeChatStaus.ON_LINE.getStatus());
    }

    @Override
    public boolean isProtocol(ChannelContext channelContext) {
        return true;
    }

    @Override
    public String name() {
        return null;
    }

    private User cacheUserInfo(VankeLoginDTO dto) {
        User user = new User();
        user.setId(dto.getSenderId());
        user.setNick(dto.getNick());
        user.setAvatar(dto.getAvatar());
        user.setStatus("online");
        user.setSign(dto.getSign());
        user.setType(dto.getType());
        user.setTerminal(dto.getTerminal());
        log.info("vanke-cacheUserInfo, user: {}", JSON.toJSONString(user));
        RedisCacheManager.getCache(ImConst.USER).put(dto.getSenderId() + ":" + Constants.USER.INFO, user);
        return user;
    }

    /**
     * IM老版需求
     * 根据Key查询缓存中是否存在用户信息 如果没有则查询数据库
     *
     * @param key
     * @return
     */
    private User getUserInfo(String key, LoginReqBody loginReqBody) {
        User user = new User();
        IMUsers imUser = userService.getUserInfo(key);
        if (imUser != null) {
            user.setId(imUser.getUuid());
            user.setNick(imUser.getNick());
            user.setAvatar(imUser.getAvatar());
            //修改用户信息
            imUserMapper.updateUserInfo(loginReqBody);
        } else {
            //如果数据库中没有该用户直接新建一个，后续采用调用第三方应用的接口进行查询
            userService.addUser(loginReqBody);
            user.setAvatar(loginReqBody.getAvatar());
            user.setNick(loginReqBody.getNick());
            user.setId(loginReqBody.getUserId());
        }
        return user;
    }

}
