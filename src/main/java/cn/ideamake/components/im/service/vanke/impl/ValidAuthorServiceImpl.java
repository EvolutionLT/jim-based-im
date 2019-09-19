package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.cache.redis.RedisCache;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.dto.mapper.*;
import cn.ideamake.components.im.pojo.constant.TermianlType;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.pojo.entity.CusChatMember;
import cn.ideamake.components.im.pojo.entity.CusChatRoomRelate;
import cn.ideamake.components.im.pojo.entity.CusInfo;
import cn.ideamake.components.im.service.vanke.ValidAuthorService;
import com.alibaba.fastjson.JSON;


import cn.ideamake.components.im.common.common.packets.LoginReqBody;
import cn.ideamake.components.im.common.common.packets.LoginRespBody;
import cn.ideamake.components.im.common.common.packets.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.*;


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
    private CusChatRoomRelateMapper cusChatRoomRelateMapper;

    @Override
    public void initUserInfo(VankeLoginDTO dto) {
        @NotBlank String senderId = dto.getSenderId();
        @NotBlank String token = dto.getToken();
        @NotNull Integer type = dto.getType();
        User user = RedisCacheManager.getCache(ImConst.USER).get(token + ":" + Constants.USER.INFO, User.class);
        if (Objects.isNull(user)) {
            Boolean isValid = false;
            //查询db，判断数据的准确性
            if (type.equals(TermianlType.CUSTOMER.getType())) {
                isValid = Optional.ofNullable(cusInfoMapper.userIsValid(senderId, token)).orElse(Boolean.FALSE);
            } else if (type.equals(TermianlType.VISITOR.getType())) {
                isValid = Optional.ofNullable(visitorMapper.userIsValid(senderId, token)).orElse(Boolean.FALSE);
            } else if (type.equals(TermianlType.ESTATE_AGENT.getType())) {
                isValid = Optional.ofNullable(userMapper.userIsValid(senderId, token)).orElse(Boolean.FALSE);
            }
            if (!isValid) {
                throw new IllegalArgumentException("VankeLoginService-validToken(), token is error, token:" + token);
            }
            //初始化数据
            cacheUserInfo(dto);
            return;
        }
        if (!Objects.equals(user.getId(), dto.getSenderId())) {
            throw new IllegalArgumentException("VankeLoginService-validToken(), userId is error, userId:" + user.getId());
        }
    }

    @Override
    public User getReceiverInfo(VankeLoginDTO dto) {
        @NotBlank String senderId = dto.getSenderId();
        String receiverId = dto.getReceiverId();
        //校验发送人合法性
        Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(dto.getSenderId() + ":" + Constants.USER.INFO, VankeLoginDTO.class)).orElseThrow(
                () -> new IllegalArgumentException("发送人userId错误，请重新登录, userId: " + senderId)
        );

        //校验接收人合法性
        if (StringUtils.isNotBlank(receiverId)) {
            return Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(dto.getToken() + ":" + Constants.USER.INFO, User.class)).orElseThrow(
                    () -> new IllegalArgumentException("接收人UserId错误，receiverId：" + receiverId)
            );
        }
        //访客类型,需要匹配聊天对象
        User userInfo = null;
        //判断有没有绑定置业顾问
        String userId = userVisitorMapper.selectUserId(receiverId);
        if (StringUtils.isNotBlank(userId)) {
            userInfo = RedisCacheManager.getCache(ImConst.USER).get(dto.getToken() + ":" + Constants.USER.INFO, User.class);
            return userInfo;
        }
        //判断有没有绑定客服, 匹配客服
        //缓存好友中有就直接返回
        RMapCache<String, User> friendsOfSender = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + senderId + ":" + Constants.USER.FRIENDS);
        if (MapUtils.isNotEmpty(friendsOfSender)) {
            for (Map.Entry<String, User> entry : friendsOfSender.entrySet()) {
                String key = entry.getKey();
                Boolean isCustomer = Optional.ofNullable(RedisCacheManager.getCache(ImConst.USER).get(key + ":" + Constants.USER.VANKE_USER_PREFIX, VankeLoginDTO.class))
                        .map(e -> e.getType() == TermianlType.CUSTOMER.getType().intValue()).orElse(Boolean.FALSE);
                if (isCustomer) {
                    return entry.getValue();
                }
            }

        }
        List<CusChatRoomRelate> roomRelates = cusChatRoomRelateMapper.selectReleates(senderId);
        if (CollectionUtils.isNotEmpty(roomRelates)) {
            //匹配到了之前联系的客服
            String id = roomRelates.stream().filter(e -> e.getType() != TermianlType.CUSTOMER.getType().intValue()).max(Comparator.comparing(CusChatRoomRelate::getId)).map(CusChatRoomRelate::getUserId).get();
            return getUserInfoByToken(id);
        }
        //白客，则匹配空闲客服
        //TODO 暂时随机分配
        List<CusChatMember> members = cusChatMemberMapper.selectCustomer();
        if (CollectionUtils.isEmpty(members)) {
            throw new IllegalArgumentException("没有空闲客服!");
        }
        int random = RandomUtils.nextInt(0, members.size());
        return getUserInfoByToken(members.get(random).getUserId());
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

    }

    @Override
    public boolean isProtocol(ChannelContext channelContext) {
        return true;
    }

    @Override
    public String name() {
        return null;
    }

    private void cacheUserInfo(VankeLoginDTO dto) {
        User user = new User();
        user.setId(dto.getSenderId());
        user.setNick(dto.getNick());
        user.setAvatar(dto.getAvatar());
        user.setStatus("online");
        user.setSign(dto.getSign());
        user.setTerminal(dto.getTerminal());
        String key1 = dto.getToken() + ":" + Constants.USER.INFO;
        String key2 = dto.getSenderId() + ":" + Constants.USER.VANKE_USER_PREFIX;
        RedisCache cache = RedisCacheManager.getCache(ImConst.USER);
        if (Objects.isNull(cache.get(key1))) {
            cache.put(key1, user);
        }
        if (Objects.isNull(cache.get(key2))) {
            cache.put(key2, dto);
        }
    }

    private User getUserInfoByToken(String id) {
        VankeLoginDTO dto = RedisCacheManager.getCache(ImConst.USER).get(id + ":" + Constants.USER.VANKE_USER_PREFIX, VankeLoginDTO.class);
        return Optional.ofNullable(dto).map(e -> {
            User user = new User();
            user.setId(e.getSenderId());
            user.setNick(e.getNick());
            user.setAvatar(e.getAvatar());
            user.setSign(e.getSign());
            user.setTerminal(e.getTerminal());
            return user;
        }).orElse(null);
    }

}
