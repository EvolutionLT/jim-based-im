package cn.ideamake.components.im.common.server.command.handler;

import cn.ideamake.components.im.common.common.*;
import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.common.common.protocol.IProtocol;
import cn.ideamake.components.im.common.common.utils.ImKit;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.server.command.AbstractCmdHandler;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.command.handler.processor.login.LoginCmdProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * 登录消息命令处理器
 *
 * @author WChao
 * @date 2018年4月10日 下午2:40:07
 */
@Slf4j

public class LoginReqHandler extends AbstractCmdHandler {

    @Override
    public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        if (packet.getBody() == null) {
            Aio.remove(channelContext, "body is null");
            return null;
        }
        List<LoginCmdProcessor> loginProcessors = this.getProcessor(channelContext, LoginCmdProcessor.class);
        if (CollectionUtils.isEmpty(loginProcessors)) {
            log.info("登录失败,没有登录命令业务处理器!");
            Aio.remove(channelContext, "no login serviceHandler processor!");
            return null;
        }
        LoginCmdProcessor loginServiceHandler = loginProcessors.get(0);
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        LoginReqBody loginReqBody = JsonKit.toBean(packet.getBody(), LoginReqBody.class);

        LoginRespBody loginRespBody = loginServiceHandler.doLogin(loginReqBody, channelContext);
        if (loginRespBody == null || loginRespBody.getUser() == null) {
            log.info("登录失败, token:{}", loginReqBody.getToken());
            if (loginRespBody == null) {
                loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10008);
            }
            loginRespBody.clear();
            ImPacket loginRespPacket = new ImPacket(Command.COMMAND_LOGIN_RESP, loginRespBody.toByte());
            ImAio.bSend(channelContext, loginRespPacket);
            ImAio.remove(channelContext, "loginName and token is incorrect");
            return null;
        }
        User user = loginRespBody.getUser();
        String userId = user.getId();
        IProtocol protocol = ImKit.protocol(null, channelContext);
        String terminal = protocol == null ? "" : protocol.name();
        user.setTerminal(terminal);
        imSessionContext.getClient().setUser(user);
        ImAio.bindUser(channelContext, userId, imConfig.getMessageHelper().getBindListener());
        //初始化绑定或者解绑群组;
        bindUnbindGroup(channelContext, user);
        loginServiceHandler.onSuccess(channelContext);
        loginRespBody.clear();
        ImPacket loginRespPacket = new ImPacket(Command.COMMAND_LOGIN_RESP, loginRespBody.toByte());
        return loginRespPacket;
    }

    /**
     * 初始化绑定或者解绑群组;
     */
    public void bindUnbindGroup(ChannelContext channelContext, User user) throws Exception {
        String userId = user.getId();
        MessageHelper messageHelper = imConfig.getMessageHelper();
        List<String> groups = messageHelper.getGroups(userId);
        if (!groups.isEmpty()) {
            groups.forEach(groupId -> {
//				groups.remove(groupId);
                Group group = messageHelper.getGroupInfo(groupId);
                ImPacket groupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ, JsonKit.toJsonBytes(group));
                try {
                    //向群聊发进入通知
                    JoinGroupReqHandler joinGroupReqHandler = CommandManager.getCommand(Command.COMMAND_JOIN_GROUP_REQ, JoinGroupReqHandler.class);
                    joinGroupReqHandler.bindGroup(groupPacket, channelContext);

                } catch (Exception e) {
                    log.error(e.toString(), e);
                }

            });

        }

//		if (!groups.isEmpty()) {
//			for (String groupId : groups) {
//				messageHelper.getBindListener().onAfterGroupUnbind(channelContext, groupId);
//			}
//		}


//
//		String userId = user.getId();
////		List<Group> groups = user.getGroups();
//		if( groups != null){
//			boolean isStore = ImConst.ON.equals(imConfig.getIsStore());
//			MessageHelper messageHelper = null;
//			List<String> groupIds = null;
//			if(isStore){
//				messageHelper = imConfig.getMessageHelper();
//				groupIds = messageHelper.getGroups(userId);
//			}
//			//绑定群组
//			for(Group group : groups){
//				if(isStore && groupIds != null){
//					groupIds.remove(group.getGroupId());
//				}
//				ImPacket groupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ, JsonKit.toJsonBytes(group));
//				try {
//					JoinGroupReqHandler joinGroupReqHandler = CommandManager.getCommand(Command.COMMAND_JOIN_GROUP_REQ, JoinGroupReqHandler.class);
//					joinGroupReqHandler.bindGroup(groupPacket, channelContext);
//				} catch (Exception e) {
//					log.error(e.toString(),e);
//				}
//			}
//			if(isStore && groupIds != null){
//				for(String groupId : groupIds){
//					messageHelper.getBindListener().onAfterGroupUnbind(channelContext, groupId);
//				}
//			}
//		}
    }

    @Override
    public Command command() {
        return Command.COMMAND_LOGIN_REQ;
    }
}
