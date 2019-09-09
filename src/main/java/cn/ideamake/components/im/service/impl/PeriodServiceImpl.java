package cn.ideamake.components.im.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.LoginReqBody;
import cn.ideamake.components.im.common.common.packets.LoginRespBody;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.common.server.command.handler.processor.login.LoginCmdProcessor;
import cn.ideamake.components.im.pojo.dto.LoginDTO;
import cn.ideamake.components.im.service.PeriodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.tio.core.ChannelContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PeriodServiceImpl implements PeriodService, LoginCmdProcessor {

    @Override
    public User getUserInfoById(String userId) {
        return new User("11111","张三");

//        String result = HttpRequest.get(Constants.GET_USER_INFO+userId).execute().body();
//        //请求获取服务内容
//        UserDTO userDTO = JSONObject.parseObject(result,UserDTO.class);
//        User user = new User();
//        BeanUtils.copyProperties(user,user);
//        return user;
    }

    @Override
    public User getUserInfoByToken(String token) {
        RMapCache<String, String> tokens = null;
        try {
            tokens = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.ITEM_LABEL.PERIOD+"::"+Constants.PEROID.USER_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new User("11111","张三");
    }

    @Override
    public String loginInfoToToken(LoginDTO loginDTO) {
        log.info("用户登录：{}",loginDTO.getUserId());
        JSONObject jsonObject = JSONUtil.parseObj(loginDTO);
        String result = HttpRequest.post(Constants.SERVER_URL.USER_LOGIN).body(jsonObject).execute().body();
        //使用前端传来的用户明和密码向应用服务器请求做验证，验证通过返回success时生成token返回给用户，并做存储，暂时先用redis的过期机制来做有效期判断
        if(Constants.RESULT_STATUS.RESULT_STATUS_SUCCESS.equals(result)){
            RMapCache<String, String> tokens = null;
            try {
                tokens = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.ITEM_LABEL.PERIOD+"::"+Constants.PEROID.USER_TOKEN);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //此处初始化用户登录信息
            User user = new User();
            String token = getUserToken();
            tokens.put(token,loginDTO.getUserId(), 30,TimeUnit.MINUTES);
            return token;
        }
        log.error("应用服务器授权失败，请检查授权方式，请求参数{}",loginDTO.toString());
        return null;
    }

    @Override
    public boolean updateUserInfo(User user) {
        return false;
    }

    @Override
    public LoginRespBody doLogin(LoginReqBody loginReqBody, ChannelContext channelContext) {
//        String loginname = loginReqBody.getUserId();
//        String password = loginReqBody.getPassword();
        ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
        String handshakeToken = imSessionContext.getToken();
        User user;
        LoginRespBody loginRespBody;
        if (StringUtils.isBlank(handshakeToken)) {
            return null;
        }
        user = this.getUserInfoByToken(handshakeToken);
        if(user == null){
            loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10008);
        }else{
            loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10007,user);
        }
        return loginRespBody;
    }

    @Override
    public void onSuccess(ChannelContext channelContext) {

    }

    @Override
    public boolean isProtocol(ChannelContext channelContext) {
        return false;
    }

    @Override
    public String name() {
        return null;
    }
    public String getUserToken(){
        return UUID.randomUUID().toString();
    }
}
