/**
 * 
 */
package cn.ideamake.components.im.common.server.command.handler.processor.login;

import cn.ideamake.components.im.common.common.packets.LoginReqBody;
import cn.ideamake.components.im.common.common.packets.LoginRespBody;
import cn.ideamake.components.im.common.server.command.handler.processor.CmdProcessor;
import org.tio.core.ChannelContext;

/**
 *
 * @author WChao
 */
public interface LoginCmdProcessor extends CmdProcessor {
	/**
	 * 执行登录操作接口方法
	 * @param loginReqBody
	 * @param channelContext
	 * @return
	 */
	public LoginRespBody doLogin(LoginReqBody loginReqBody, ChannelContext channelContext);

	/**
	 * 登录成功回调方法
	 * @param channelContext
	 */
	public void onSuccess(ChannelContext channelContext);
}
