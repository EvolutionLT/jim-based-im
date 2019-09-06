package cn.ideamake.components.im.config;

import cn.ideamake.components.im.components.IMWsHandshakeProcessor;
import cn.ideamake.components.im.service.impl.LoginServiceProcessor;
import org.apache.commons.lang3.StringUtils;
import org.jim.common.ImConfig;
import org.jim.common.ImConst;
import org.jim.common.config.PropertyImConfigBuilder;
import org.jim.common.packets.Command;
import org.jim.common.utils.PropUtil;
import org.jim.server.ImServerStarter;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.HandshakeReqHandler;
import org.jim.server.command.handler.LoginReqHandler;
import org.jim.server.listener.ImGroupListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tio.core.ssl.SslConfig;

import java.io.IOException;

/**
 * im-server 配置类
 */
@Configuration
public class IMConfig {

    @Bean
    public ImServerStarter imServerStarter() throws IOException {
        ImConfig imConfig = new PropertyImConfigBuilder("jim.properties").build();
        //初始化SSL;(开启SSL之前,你要保证你有SSL证书哦...)
//        initSsl(imConfig);
        //设置群组监听器，非必须，根据需要自己选择性实现;
        imConfig.setImGroupListener(new ImGroupListener());
        ImServerStarter imServerStarter = new ImServerStarter(imConfig);
        /*****************start 以下处理器根据业务需要自行添加与扩展，每个Command都可以添加扩展,此处为demo中处理**********************************/
        HandshakeReqHandler handshakeReqHandler = CommandManager.getCommand(Command.COMMAND_HANDSHAKE_REQ, HandshakeReqHandler.class);
        //添加自定义握手处理器;
        handshakeReqHandler.addProcessor(new IMWsHandshakeProcessor());
        LoginReqHandler loginReqHandler = CommandManager.getCommand(Command.COMMAND_LOGIN_REQ, LoginReqHandler.class);
        //添加登录业务处理器;
        loginReqHandler.addProcessor(new LoginServiceProcessor());
        /*****************end *******************************************************************************************/
        imServerStarter.start();
        return imServerStarter;
    }

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