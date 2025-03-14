/**
 *
 */
package cn.ideamake.components.im.common.common.protocol;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.packets.Command;
import org.tio.core.ChannelContext;

/**
 * 转换不同协议消息包;
 * @author WChao
 *
 */
public interface IConvertProtocolPacket {
    /**
     * 转化请求包
     * @param body
     * @param command
     * @param channelContext
     * @return
     */
    public ImPacket ReqPacket(byte[] body, Command command, ChannelContext channelContext);

    /**
     * 转化响应包
     * @param body
     * @param command
     * @param channelContext
     * @return
     */
    public ImPacket RespPacket(byte[] body, Command command, ChannelContext channelContext);
}
