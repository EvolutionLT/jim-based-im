package cn.ideamake.components.im.listener;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.cache.redis.RedisCache;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.common.common.packets.Client;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.common.common.utils.ChatKit;
import cn.ideamake.components.im.common.common.utils.ImKit;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.durables.DurableUtils;
import cn.ideamake.components.im.durables.channel.MysqlDataCrud;
import cn.ideamake.components.im.pojo.constant.VankeChatStaus;
import cn.ideamake.components.im.service.vanke.AysnChatService;
import com.alibaba.fastjson.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

import javax.annotation.Resource;

import static com.jfinal.plugin.ehcache.CacheKit.get;

@Slf4j
@Service
public class ImServerAioListener implements ServerAioListener {


    @Setter
    private ImConfig imConfig;

    @Resource
    private AysnChatService aysnChatService;

    @Resource
    private MysqlDataCrud mysqlDataCrud;


    public ImServerAioListener() {
    }

    /**
     * 建链后触发本方法，注：建链不一定成功，需要关注参数isConnected
     *
     * @param channelContext
     * @param isConnected    是否连接成功,true:表示连接成功，false:表示连接失败
     * @param isReconnect    是否是重连, true: 表示这是重新连接，false: 表示这是第一次连接
     * @throws Exception
     * @author: WChao
     */
    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
        //异步修改成员在线状态
        if(isConnected) {
            aysnChatService.synUpdateMember(channelContext.getUserid(), VankeChatStaus.ON_LINE.getStatus());
        }
    }

    /**
     * 消息包发送之后触发本方法
     *
     * @param channelContext
     * @param packet
     * @param isSentSuccess  true:发送成功，false:发送失败
     * @throws Exception
     * @author WChao
     */
    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
        //TODO userId 为空

    }

    /**
     * 连接关闭前触发本方法
     *
     * @param channelContext the channelcontext
     * @param throwable      the throwable 有可能为空
     * @param remark         the remark 有可能为空
     * @param isRemove
     * @throws Exception
     * @author WChao
     */
    @Override
    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
        log.info("关闭连接之前，userId: {}", channelContext.getUserid());
        if (imConfig == null) {
            return;
        }
        MessageHelper messageHelper = imConfig.getMessageHelper();
        if (messageHelper != null) {
            ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
            if (imSessionContext == null) {
                return;
            }
            Client client = imSessionContext.getClient();
            if (client == null) {
                return;
            }
            User onlineUser = client.getUser();
            if (onlineUser == null) {
                return;
            }
            log.info("关闭连接之前，修改redis中用户的状态为下线，userId: {}, onlineUser: {}", channelContext.getUserid(), JSON.toJSONString(onlineUser));
            messageHelper.getBindListener().initUserTerminal(channelContext, onlineUser.getTerminal(), ImConst.OFFLINE);
            //异步修改成员在线状态
            aysnChatService.synUpdateMember(channelContext.getUserid(), VankeChatStaus.OFF_LINE.getStatus());
        }
    }

    /**
     * 解码成功后触发本方法
     *
     * @param channelContext
     * @param packet
     * @param packetSize
     * @throws Exception
     * @author: WChao
     */
    @Override
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {

    }

    /**
     * 接收到TCP层传过来的数据后
     *
     * @param channelContext
     * @param receivedBytes  本次接收了多少字节
     * @throws Exception
     */
    @Override
    public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {

    }

    /**
     * 处理完一个消息包的后续操作
     *
     * @param channelContext
     * @param packet
     * @param cost           本次处理消息耗时，单位：毫秒
     * @throws Exception
     */
    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
        ImPacket imPacket = (ImPacket) packet;
        ChatBody chatBody = ChatKit.toChatBody(imPacket.getBody(), channelContext);
        //进入IM业务逻辑
        if(chatBody!=null && chatBody.getRoomId()!=null){
            mysqlDataCrud.insertMsgData(chatBody);
        }else{
            //进入客服业务逻辑
            //此处做好友关系处理,暂时对每条消息都检查用户好友关系，没有就做添加处理,用户只有再授权登录后才会再im系统中被记录
            if (chatBody != null && StringUtils.isNotBlank(chatBody.getFrom()) && StringUtils.isNotBlank(chatBody.getTo())) {
                RedisCache userCache = RedisCacheManager.getCache(ImConst.USER);
                aysnChatService.synAddChatRecord(chatBody,imPacket.getCommand().getNumber());
                String keySender = chatBody.getFrom() + ":" + Constants.USER.INFO;
                //发送者信息未被初始化,正常情况下应用和im双方用户数据需要打通，暂不考虑发送方不存在的情况，会被记录，但是不纪录用户列表
                if (userCache.get(keySender,User.class) == null) {
                    log.error("发送者{}信息未被初始化", chatBody.getFrom());
                    //此处后续可以向三方服务器拉取用户信息
                    return;
                }
                String keyReceiver = chatBody.getTo() + ":" + Constants.USER.INFO;
                if (userCache.get(keyReceiver,User.class) == null) {
                    log.error("接收者{}信息未被初始化", chatBody.getTo());
                }
            }
        }
    }
}
