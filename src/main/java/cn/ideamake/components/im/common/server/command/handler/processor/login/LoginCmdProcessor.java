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
	 * 当登陆失败时设置user属性需要为空，相反登陆成功user属性是必须非空的;
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
