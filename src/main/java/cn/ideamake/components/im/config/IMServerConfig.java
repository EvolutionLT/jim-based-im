package cn.ideamake.components.im.config;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.config.PropertyImConfigBuilder;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.utils.PropUtil;
import cn.ideamake.components.im.common.server.ImServerStarter;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.command.handler.HandshakeReqHandler;
import cn.ideamake.components.im.common.server.command.handler.LoginReqHandler;
import cn.ideamake.components.im.common.server.helper.redis.RedisMessageHelper;
import cn.ideamake.components.im.components.IMWsHandshakeProcessor;
import cn.ideamake.components.im.service.PeriodService;
import cn.ideamake.components.im.service.impl.PeriodServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tio.core.intf.GroupListener;
import org.tio.core.ssl.SslConfig;
import org.tio.server.intf.ServerAioListener;

import java.io.IOException;

/**
 * im-server 配置类
 */
@Configuration
public class IMServerConfig {

//    @Autowired
//    private ServerAioListener serverAioListener;
//
//    @Autowired
//    private GroupListener groupListener;
//
//    @Autowired
//    private RedisMessageHelper redisMessageHelper;
//
//    @Autowired
//    private PeriodService periodService;
//
//    @Bean
//    public ImServerStarter imServerStarter() throws IOException {
//        ImConfig imConfig = new PropertyImConfigBuilder("jim.properties").build();
//        //初始化SSL;(开启SSL之前,你要保证你有SSL证书哦...)
////        initSsl(imConfig);
//        //设置群组监听器，非必须，根据需要自己选择性实现;
//        imConfig.setImGroupListener(groupListener);
//        ImServerStarter imServerStarter = new ImServerStarter(imConfig,serverAioListener);
//        /*****************start 以下处理器根据业务需要自行添加与扩展，每个Command都可以添加扩展,此处为demo中处理**********************************/
//        HandshakeReqHandler handshakeReqHandler = CommandManager.getCommand(Command.COMMAND_HANDSHAKE_REQ, HandshakeReqHandler.class);
//        //添加自定义握手处理器;
//        handshakeReqHandler.addProcessor(new IMWsHandshakeProcessor());
//        LoginReqHandler loginReqHandler = CommandManager.getCommand(Command.COMMAND_LOGIN_REQ, LoginReqHandler.class);
//
//        //添加登录业务处理器;
//        loginReqHandler.addProcessor(new PeriodServiceImpl());
//
//        //添加持久话消息处理
//        imConfig.setMessageHelper(redisMessageHelper);
//
//        /*****************end *******************************************************************************************/
//        imServerStarter.start();
//        return imServerStarter;
//    }

    /**
     * 开启SSL之前，你要保证你有SSL证书哦！
     *
     * @param imConfig
     * @throws Exception
     */
    private static void initSsl(ImConfig imConfig) throws Exception {
        //开启SSL
        if (ImConst.ON.equals(imConfig.getIsSSL())) {
            String keyStorePath = PropUtil.get("jim.key.store.path");
            String keyStoreFile = keyStorePath;
            String trustStoreFile = keyStorePath;
            String keyStorePwd = PropUtil.get("jim.key.store.pwd");
            if (StringUtils.isNotBlank(keyStoreFile) && StringUtils.isNotBlank(trustStoreFile)) {
                SslConfig sslConfig = SslConfig.forServer(keyStoreFile, trustStoreFile, keyStorePwd);
                imConfig.setSslConfig(sslConfig);
            }
        }


    }
}