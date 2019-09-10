package cn.ideamake.components.im.listener;

import cn.ideamake.components.im.common.common.ImAio;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.packets.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;

@Service
@Slf4j
public class ImGroupListener implements GroupListener {

    @Override
    public void onAfterBind(ChannelContext channelContext, String s) throws Exception {

    }

    /**
     * @param channelContext
     * @param group
     * @throws Exception
     */
    @Override
    public void onAfterUnbind(ChannelContext channelContext, String group) throws Exception {
        //发退出房间通知  COMMAND_EXIT_GROUP_NOTIFY_RESP
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
        ExitGroupNotifyRespBody exitGroupNotifyRespBody = new ExitGroupNotifyRespBody();
        exitGroupNotifyRespBody.setGroup(group);
        Client client = imSessionContext.getClient();
        if (client == null) {
            return;
        }
        User clientUser = client.getUser();
        if (clientUser == null) {
            return;
        }
        User notifyUser = new User(clientUser.getId(), clientUser.getNick());
        exitGroupNotifyRespBody.setUser(notifyUser);

        RespBody respBody = new RespBody(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, exitGroupNotifyRespBody);
        ImPacket imPacket = new ImPacket(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, respBody.toByte());
        ImAio.sendToGroup(group, imPacket);


    }
}