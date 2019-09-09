package cn.ideamake.components.im.listener;

//import cn.ideamake.im.durables.DurableUtils;
//import cn.ideamake.im.utils.BasicConstants;
//import cn.ideamake.im.utils.RedisUtil;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.server.listener.ImServerAioListener;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

/**
 * @author evolution
 * @title: ImServerListener
 * @projectName im
 * @description: TODO 自定义IM 监听类
 * @date 2019-07-05 11:06
 * @ltd：思为
 */
@Component
public class ImServerMsgListener extends ImServerAioListener {


//    private static DurableUtils durableUtils= DurableUtils.getInstance();
//            //= (DurableUtils)SpringContextUtil.getBean("durableUtils");
//
//    private static RedisUtil redisUtil = RedisUtil.getInstance();

    /**
     * @param channelContext 通道
     * @param packet         消息发送包
     * @param isSentSuccess  是否发送成功
     */
    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
        super.onAfterSent(channelContext, packet, isSentSuccess);
    }


    /**
     * 监听消息  用于接收消息进行数据持久化保存
     *
     * @param channelContext
     * @param packet
     * @param cost
     * @throws Exception
     */
    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
        ImPacket imPacket = (ImPacket) packet;
        ChatBody chatBody = ChatKit.toChatBody(imPacket.getBody(), channelContext);
        // IMChatBody chatBody1=chatBody;
//        if(chatBody!=null && chatBody.getCmd()==11){
//            durableUtils.insertMsgData(chatBody);
//        }

    }


    /**
     * 关闭连接时触发方法 用于清除redis中用户数据
     *
     * @param channelContext
     * @param throwable
     * @param remark
     * @param isRemove
     */
    @Override
    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
//        redisUtil.del(BasicConstants.IMUSERKEY+channelContext.getUserid());
        super.onBeforeClose(channelContext, throwable, remark, isRemove);
    }
}
