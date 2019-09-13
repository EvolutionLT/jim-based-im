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
import cn.ideamake.components.im.service.impl.PeriodServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.tio.server.AioServer;
import org.tio.server.intf.ServerAioListener;

import javax.annotation.PostConstruct;
import java.io.IOException;

//import cn.ideamake.components.im.common.server.listener.ImGroupListener;
//import cn.ideamake.components.im.common.server.listener.ImServerAioListener;

//import cn.ideamake.components.im.common.server.listener.ImServerAioListener;
//import cn.ideamake.components.im.listener.ImServerAioListener;

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
//	private ImConfig imConfig = null;
	
//	public ImServerStarter(ImConfig imConfig){
//		this(imConfig,null);
//	}

//	public ImServerStarter(ImConfig imConfig, ServerAioListener imAioListener){
//		this.imConfig = imConfig;
//		this.imAioListener = imAioListener;
//		init();
//	}

//	@Autowired
//    private ImServerAioListener imServerAioListener;

	@Autowired
	private RedisMessageHelper redisMessageHelper;

	@Autowired
	private PeriodServiceImpl periodService;


	@PostConstruct
	public void init(){
		ImConfig imConfig = new PropertyImConfigBuilder("jim.properties").build();

		System.setProperty("tio.default.read.buffer.size", String.valueOf(imConfig.getReadBufferSize()));

		//此bean暂时手工注入
//		imAioHandler = new ImServerAioHandler(imConfig) ;


		imServerGroupContext = new ImServerGroupContext(imConfig,imAioHandler, imAioListener);
		imServerGroupContext.setGroupListener(imGroupListener);

//		if(imConfig.getMessageHelper() == null){
//			imConfig.setMessageHelper(new RedisMessageHelper());
//		}
		HandshakeReqHandler handshakeReqHandler = CommandManager.getCommand(Command.COMMAND_HANDSHAKE_REQ, HandshakeReqHandler.class);
		//添加自定义握手处理器;
		handshakeReqHandler.addProcessor(new IMWsHandshakeProcessor());
		imConfig.setMessageHelper(redisMessageHelper);

		//添加登录业务处理器;
		LoginReqHandler loginReqHandler = CommandManager.getCommand(Command.COMMAND_LOGIN_REQ, LoginReqHandler.class);
		loginReqHandler.addProcessor(periodService);

		//开启SSL
//		if(ImConst.ON.equals(imConfig.getIsSSL())){
//			SslConfig sslConfig = imConfig.getSslConfig();
//			if(sslConfig != null) {
//				imServerGroupContext.setSslConfig(sslConfig);
//			}
//		}
		aioServer = new AioServer(imServerGroupContext);
		//添加持久话消息处理
		redisMessageHelper.setImConfig(imConfig);
		imAioHandler.setImConfig(imConfig);
		imAioListener.setImConfig(imConfig);



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
