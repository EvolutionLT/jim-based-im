package cn.ideamake.components.im.common.server.command.handler.processor.group;

import cn.ideamake.components.im.common.common.packets.Group;
import cn.ideamake.components.im.common.common.packets.JoinGroupRespBody;
import cn.ideamake.components.im.common.server.command.handler.processor.CmdProcessor;
import org.tio.core.ChannelContext;

/**
 * @author ensheng
 */
public interface GroupCmdProcessor extends CmdProcessor {
    /**
     * 加入群组处理
     *
     * @param joinGroup
     * @param channelContext
     * @return
     */
    JoinGroupRespBody join(Group joinGroup, ChannelContext channelContext);
}
