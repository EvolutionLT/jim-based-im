package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.common.exception.BusinessException;
import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
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
import cn.ideamake.components.im.service.vanke.AysnChatService;
import cn.ideamake.components.im.service.vanke.ValidAuthorService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

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

//    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50));


    private static final int EXPIRE_TIME = 7 * 24 * 60;

    private static final long lOCK_EXPIRE_TIME = 2 * 60L;


    @Override
    public void initUserInfo(VankeLoginDTO dto) {
        @NotBlank String senderId = dto.getSenderId();
        @NotBlank String token = dto.getToken();
        @NotNull Integer type = dto.getType();
        User user = RedisCacheManager.getCache(ImConst.USER).get(senderId + ":" + Constants.USER.INFO, User.class);
        if (Objects.isNull(user)) {
            valid(senderId, token, type);
            //初始化数据
            cacheUserInfo(dto);
            aysnChatService.synInitChatMember(dto);
            return;
        }
        if (!Objects.equals(user.getId(), dto.getSenderId())) {
            throw new IllegalArgumentException("VankeLoginService-validToken(), userId is error, userId:" + user.getId());
        }
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
        //校验发送人合法性
        User user = RedisCacheManager.getCache(ImConst.USER).get(senderId + ":" + Constants.USER.INFO, User.class);
        if (Objects.isNull(user)) {
            valid(senderId, token, type);
            //初始化数据
            user = cacheUserInfo(dto);
        }
        //校验接收人合法性
        if (StringUtils.isNotBlank(receiverId)) {
            return Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(dto.getToken() + ":" + Constants.USER.INFO, User.class)).orElseThrow(
                    () -> new BusinessException(500, "接收人错误,请重新发送!" + receiverId)
            );
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


    private User getRandomCustomer(VankeLoginDTO dto, User user) {
        List<CusChatMember> members = cusChatMemberMapper.selectCustomer();
        if (CollectionUtils.isEmpty(members)) {
            throw new IllegalArgumentException("当前客服繁忙，请耐心等待!");
        }
        int random = RandomUtils.nextInt(0, members.size());
        String to = members.get(random).getUserId();
        dto.setReceiverId(to);
//        RedisCacheManager.getCache(ImConst.USER).incr(String.format(VankeRedisKey.VANKE_CHAT_MEMBER_NUM_KEY, to), 1);
        User friend = cacheFriend(dto);
        // 模拟访客给客服发送消息
//        ImAio.sendToUser(user, friend.getId(), "您好, 我想咨询下楼盘信息！", "false");
        // 模拟客服给访客发送消息
//        CompletableFuture.runAsync(() -> {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                //TODO
//            }
////            ImAio.sendToUser(friend, user.getId(), "您好! 我是万科置业客服，有什么可以帮您!", "false");
//        }, threadPoolExecutor);
        return friend;
    }

    private User cacheFriend(VankeLoginDTO dto) {
        @NotBlank String senderId = dto.getSenderId();
        @NotBlank String to = dto.getReceiverId();
        User friend = Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(to + ":" + Constants.USER.INFO, User.class)).orElseThrow(() -> new IMException(RestEnum.USER_NOT_FOUND));
        //快速出基本功能，使用jim原先好友存储List<User>，后续改成RMapCache<String,User>形式，便于做消息更新；
        RMapCache<String, User> friendsOfSender = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + senderId + ":" + Constants.USER.FRIENDS);
        //发送者好友列表没有接收者时，将接收者添加到其好友列表
        if (friendsOfSender.isEmpty() || !friendsOfSender.containsKey(senderId)) {
            friendsOfSender.put(to, friend);
        }
        RMapCache<String, User> friendsOfReceiver = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + to + ":" + Constants.USER.FRIENDS);
        //同样检查接收者好友列表，若没有发送时，将接收者添加到其好友列表
        if (friendsOfReceiver.isEmpty() || !friendsOfReceiver.containsKey(to)) {
            User user = Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(senderId + ":" + Constants.USER.INFO, User.class)).orElseThrow(() -> new IMException(RestEnum.USER_NOT_FOUND));
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
        String token = loginReqBody.getToken();
        if (StringUtils.isBlank(token)) {
            log.info(logStr + "token is null!");
            return null;
        }
        User user = RedisCacheManager.getCache(ImConst.USER).get(token + ":" + Constants.USER.INFO, User.class);
        log.info(logStr + "result: {}", JSON.toJSONString(user));
        return Objects.isNull(user) ? new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10008) : new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10007, user);
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
        RedisCacheManager.getCache(ImConst.USER).put(dto.getSenderId() + ":" + Constants.USER.INFO, user);
        return user;
    }

}
