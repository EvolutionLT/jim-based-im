package cn.ideamake.components.im.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.id.impl.UUIDSessionIdGenerator;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.common.common.utils.ImKit;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.common.server.command.handler.processor.login.LoginCmdProcessor;
import cn.ideamake.components.im.pojo.dto.LoginDTO;
import cn.ideamake.components.im.service.PeriodService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PeriodServiceImpl implements PeriodService, LoginCmdProcessor {

    @Override
    public User getUserInfoById(String userId) {
        return new User("11111", "张三");

//        String result = HttpRequest.get(Constants.GET_USER_INFO+userId).execute().body();
//        //请求获取服务内容
//        UserDTO userDTO = JSONObject.parseObject(result,UserDTO.class);
//        UserInfo user = new UserInfo();
//        BeanUtils.copyProperties(user,user);
//        return user;
    }

    /**
     * 目前方式是只要用户通过应用服务授权就可以获取一个半小时代表用户身份的token，用户可以多次获取，后续可以根据实际业务做调整
     * @param token
     * @return
     */
    @Override
    public User getUserInfoByToken(String token) {
        RMapCache<String, User> tokens = null;
        try {
            tokens = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.ITEM_LABEL.PERIOD + "::" + Constants.PEROID.USER_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(tokens==null){
            log.warn("tokens map is  empty");
            return null;
        }
        return tokens.get(token);
    }

    @Override
    public String loginInfoToToken(LoginDTO loginDTO) {
        log.info("用户登录：{}", loginDTO.getUserId());
        JSONObject jsonObject = JSONUtil.parseObj(loginDTO);
        String result = HttpRequest.post(Constants.SERVER_URL.USER_LOGIN).body(jsonObject).execute().body();
        Rest<User> jsonObjects  =JSON.parseObject(result,new TypeReference<Rest<User>>(){});
//        if (jsonResult.get("code") != null && jsonResult.get("code").equals(0) && jsonResult.get("data") != null) {
        if (jsonObjects != null && jsonObjects.getCode() !=null && jsonObjects.getCode().equals(0) && jsonObjects.getData()!=null) {
            //此处初始化用户登录信息,目前暂定验证成功后，由应用方返回用户信息，im服务做限时存储，
            User user = jsonObjects.getData();
//            user.setGroups(initGroups(user));
//            user.setFriends(initFriends(user));
            //使用前端传来的用户明和密码向应用服务器请求做验证，验证通过返回用户的基本信息，im服务生成token返回给用户，并做存储，暂时先用redis的过期机制来做有效期判断
            RMapCache<String,User> tokens = null;
            try {
                tokens = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.ITEM_LABEL.PERIOD + "::" + Constants.PEROID.USER_TOKEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String token = getUserToken();
            tokens.put(token, user, 30, TimeUnit.MINUTES);

            //初始化用户信息
            User loginInfoSimple = ImKit.copyUserWithoutFriendsGroups(user);
            RedisCacheManager.getCache(ImConst.USER).put(user.getId()+":"+Constants.USER.INFO,loginInfoSimple);
            return token;
        }
        log.error("应用服务器授权失败，请检查授权方式，请求参数{}", loginDTO.toString());
        return null;
    }


    /**
     * 目前用户关系使用非关系数据库存储，好友关系是相互的
     * 第一版暂这个功能点往后排
     * 有用户信息变更要同步变更用户好友列表信息
     * 这个更改可以异步处理
     * 后续再优化方案#TODO
     *
     * @param user
     * @return
     */
    @Override
    public boolean updateUserInfo(User user) {
        return false;
    }

    /**
     * 当登陆失败时设置user属性需要为空，相反登陆成功user属性是必须非空的;
     * @param loginReqBody
     * @param channelContext
     * @return
     */
    @Override
    public LoginRespBody doLogin(LoginReqBody loginReqBody, ChannelContext channelContext) {
//        String loginname = loginReqBody.getUserId();
//        String password = loginReqBody.getPassword();
        //从websocket连接中获取信息
//        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        String handshakeToken = loginReqBody.getToken();
        User user;
        LoginRespBody loginRespBody;
        if (StringUtils.isBlank(handshakeToken)) {
            return null;
        }
        user = this.getUserInfoByToken(handshakeToken);

        if (user == null) {
            loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10008);
        } else {
            loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10007, user);
        }
        loginRespBody.setUser(user);
        return loginRespBody;
    }

    @Override
    public void onSuccess(ChannelContext channelContext) {
        log.info("登录成功回调");
////		logger.info("登录成功回调方法");
//		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
//		UserInfo user = imSessionContext.getClient().getUser();
//		if(user.getGroups() != null){
//			for(Group group : user.getGroups()){//发送加入群组通知
//				ImPacket groupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ, JsonKit.toJsonBytes(group));
//				try {
//					JoinGroupReqHandler joinGroupReqHandler = CommandManager.getCommand(Command.COMMAND_JOIN_GROUP_REQ, JoinGroupReqHandler.class);
//					joinGroupReqHandler.joinGroupNotify(groupPacket, channelContext);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
    }

    @Override
    public boolean isProtocol(ChannelContext channelContext) {
        return true;
    }

    @Override
    public String name() {
        return "default";
    }

    public String getUserToken() {
        return UUID.randomUUID().toString();
    }

//    public List<Group> initGroups(UserInfo user){
//        //模拟的群组;正式根据业务去查数据库或者缓存;
//        List<Group> groups = new ArrayList<Group>();
//        groups.add(new Group("100","我的好友"));
//        return groups;
//    }

    /**
     * 默认有发过消息就做对应的好友添加
     * @param user
     * @return
     */
//    public List<Group> initFriends(User user){
//        List<>
//        List<Group> friends = new ArrayList<Group>();
//        Group myFriend = new Group("1","我的好友");
//        List<User> myFriendGroupUsers = new ArrayList<User>();
//        UserInfo user1 = new UserInfo();
//
//        //此处初始化用户,通过登录的用户id从真实数据库中查询，暂时打算远程调用业务端接口，封装一个http客户端#TODO
//        user1.setId(UUIDSessionIdGenerator.nstance.sessionId(null));
//        myFriendGroupUsers.add(user1);
//        myFriend.setUsers(myFriendGroupUsers);
//        friends.add(myFriend);
//        return friends;
//    }
}
