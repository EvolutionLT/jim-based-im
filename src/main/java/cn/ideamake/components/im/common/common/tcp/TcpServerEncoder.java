/**
 *
 */
package cn.ideamake.components.im.common.common.tcp;

import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.Protocol;
import cn.ideamake.components.im.common.common.tcp.TcpPacket;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年8月21日 下午4:00:31
 */
public class TcpServerEncoder {

    public static ByteBuffer encode(TcpPacket tcpPacket, GroupContext groupContext, ChannelContext channelContext) {
        int bodyLen = 0;
        byte[] body = tcpPacket.getBody();
        if (body != null) {
            bodyLen = body.length;
        }
        boolean isCompress = true;
        boolean is4ByteLength = true;
        boolean isEncrypt = true;
        boolean isHasSynSeq = tcpPacket.getSynSeq() > 0;
        //协议版本号
        byte version = Protocol.VERSION;

        //协议标志位mask
        byte maskByte = ImPacket.encodeEncrypt(version, isEncrypt);
        maskByte = ImPacket.encodeCompress(maskByte, isCompress);
        maskByte = ImPacket.encodeHasSynSeq(maskByte, isHasSynSeq);
        maskByte = ImPacket.encode4ByteLength(maskByte, is4ByteLength);
        byte cmdByte = 0x00;
        //消息类型;
        if (tcpPacket.getCommand() != null) {
            cmdByte = (byte) (cmdByte | tcpPacket.getCommand().getNumber());
        }

        tcpPacket.setVersion(version);
        tcpPacket.setMask(maskByte);

        //byteBuffer的总长度是 = 1byte协议版本号+1byte消息标志位+4byte同步序列号(如果是同步发送则都4byte同步序列号,否则无4byte序列号)+1byte命令码+4byte消息的长度+消息体
        int allLen = 1 + 1;
        if (isHasSynSeq) {
            allLen += 4;
        }
        allLen += 1 + 4 + bodyLen;
        ByteBuffer buffer = ByteBuffer.allocate(allLen);
        //设置字节序
        ByteOrder byteOrder = groupContext == null ? ByteOrder.BIG_ENDIAN : groupContext.getByteOrder();
        buffer.order(byteOrder);
        buffer.put(tcpPacket.getVersion());
        buffer.put(tcpPacket.getMask());
        //同步发送设置4byte，同步序列号;
        if (isHasSynSeq) {
            buffer.putInt(tcpPacket.getSynSeq());
        }
        buffer.put(cmdByte);
        buffer.putInt(bodyLen);
        buffer.put(body);
        return buffer;
    }
}
