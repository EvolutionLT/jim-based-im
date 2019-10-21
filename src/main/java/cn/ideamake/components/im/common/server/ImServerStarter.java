/**
 * 
 */
package cn.ideamake.components.im.common.server;


import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.config.PropertyImConfigBuilder;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.command.handler.HandshakeReqHandler;
import cn.ideamake.components.im.common.server.command.handler.LoginReqHandler;
import cn.ideamake.components.im.common.server.handler.ImServerAioHandler;
import cn.ideamake.components.im.common.server.helper.redis.RedisMessageHelper;
import cn.ideamake.components.im.components.IMWsHandshakeProcessor;
import cn.ideamake.components.im.listener.ImGroupListener;
import cn.ideamake.components.im.listener.ImServerAioListener;
//import cn.ideamake.components.im.service.impl.PeriodServiceImpl;
import cn.ideamake.components.im.service.vanke.VankeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.tio.server.AioServer;

import javax.annotation.PostConstruct;
import java.io.IOException;


/**
 * 
 * @author WChao
 *
 */
@Configuration
@Slf4j
public class ImServerStarter {

	@Autowired
	private ImServerAioHandler imAioHandler;

	/**
	 * 消息包拦截监听处理
	 */
	@Autowired
	private ImServerAioListener imAioListener;


	private ImServerGroupContext imServerGroupContext = null;

	/**
	 * 群组消息监听处理
	 */
	@Autowired
	private ImGroupListener imGroupListener;
	private AioServer aioServer;

	@Autowired
	private RedisMessageHelper redisMessageHelper;

//	@Autowired
//	private PeriodServiceImpl periodService;

	@Autowired
	private VankeService vankeLoginService;


	@PostConstruct
	public void init(){
		ImConfig imConfig = new PropertyImConfigBuilder("jim.properties").build();

		System.setProperty("tio.default.read.buffer.size", String.valueOf(imConfig.getReadBufferSize()));

		//此bean暂时手工注入
//		imAioHandler = new ImServerAioHandler(imConfig) ;
		//添加持久话消息处理

		imAioHandler.setImConfig(imConfig);
		imAioListener.setImConfig(imConfig);
		imConfig.setImGroupListener(imGroupListener);

		//初始化上下文
		imServerGroupContext = new ImServerGroupContext(imConfig,imAioHandler, imAioListener);
		imServerGroupContext.setGroupListener(imGroupListener);

		//添加自定义握手处理器;
		HandshakeReqHandler handshakeReqHandler = CommandManager.getCommand(Command.COMMAND_HANDSHAKE_REQ, HandshakeReqHandler.class);
		handshakeReqHandler.addProcessor(new IMWsHandshakeProcessor());

		//持久化类设置
		imConfig.setMessageHelper(redisMessageHelper);
		redisMessageHelper.setImConfig(imConfig);

		//添加登录业务处理器;
		LoginReqHandler loginReqHandler = CommandManager.getCommand(Command.COMMAND_LOGIN_REQ, LoginReqHandler.class);
		loginReqHandler.addProcessor(vankeLoginService);

		aioServer = new AioServer(imServerGroupContext);




		/*****************end *******************************************************************************************/
        try {
            aioServer.start(imConfig.getBindIp(),imConfig.getBindPort());
        } catch (IOException e) {
            log.error(e.getMessage());
        }


//        System.setProperty("tio.default.read.buffer.size", String.valueOf(imConfig.getReadBufferSize()));
//        imAioHandler = new ImServerAioHandler(imConfig) ;
//        if(imAioListener == null){
//            imAioListener = new ImServerAioListener(imConfig);
//        }
//        GroupListener groupListener = imConfig.getImGroupListener();
//        if(groupListener == null){
//            imConfig.setImGroupListener(new ImGroupListener());
//        }
//        this.imGroupListener = (ImGroupListener)imConfig.getImGroupListener();
//        imServerGroupContext = new ImServerGroupContext(imConfig,imAioHandler, imAioListener);
//        imServerGroupContext.setGroupListener(imGroupListener);
//        if(imConfig.getMessageHelper() == null){
//            imConfig.setMessageHelper(new RedisMessageHelper(imConfig));
//        }
//        //开启SSL
//        if(ImConst.ON.equals(imConfig.getIsSSL())){
//            SslConfig sslConfig = imConfig.getSslConfig();
//            if(sslConfig != null) {
//                imServerGroupContext.setSslConfig(sslConfig);
//            }
//        }
//        aioServer = new AioServer(imServerGroupContext);

	}
	
//	public void start() throws IOException {
//		aioServer.start(this.imConfig.getBindIp(),this.imConfig.getBindPort());
//	}
	
	public void stop(){
		aioServer.stop();
	}
}
