/**
 *
 */
package cn.ideamake.components.im.common.server.handler;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.exception.ImException;
import cn.ideamake.components.im.common.common.protocol.IProtocol;
import cn.ideamake.components.im.common.common.utils.ImKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;

/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年8月3日 下午2:40:24
 */
@Slf4j
public class ProtocolHandlerManager {


    private static Map<String, AbstractProtocolHandler> serverHandlers = new HashMap<String, AbstractProtocolHandler>();

    static {
        try {
//			List<ProtocolHandlerConfiguration> configurations = ProtocolHandlerConfigurationFactory.parseConfiguration();
            //文件找不到路径，暂时在此处写死#TODO
            List<ProtocolHandlerConfiguration> configurations = new ArrayList<ProtocolHandlerConfiguration>() {{
                add(new ProtocolHandlerConfiguration("http" , "cn.ideamake.components.im.common.server.http.HttpProtocolHandler"));
                add(new ProtocolHandlerConfiguration("tcp" , "cn.ideamake.components.im.common.server.tcp.TcpProtocolHandler"));
                add(new ProtocolHandlerConfiguration("ws" , "cn.ideamake.components.im.common.server.ws.WsProtocolHandler"));
            }};
            init(configurations);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    private static void init(List<ProtocolHandlerConfiguration> configurations) throws Exception {


        for (ProtocolHandlerConfiguration configuration : configurations) {
            Class<AbstractProtocolHandler> serverHandlerClazz = (Class<AbstractProtocolHandler>) Class.forName(configuration.getServerHandler());
            AbstractProtocolHandler serverHandler = serverHandlerClazz.newInstance();
            addServerHandler(serverHandler);
        }
    }

    public static AbstractProtocolHandler addServerHandler(AbstractProtocolHandler serverHandler) throws ImException {
        if (Objects.isNull(serverHandler)) {
            throw new ImException("ProtocolHandler must not null ");
        }
        return serverHandlers.put(serverHandler.protocol().name(), serverHandler);
    }

    public static AbstractProtocolHandler removeServerHandler(String name) throws ImException {
        if (StringUtils.isEmpty(name)) {
            throw new ImException("server name must not empty");
        }
        return serverHandlers.remove(name);
    }

    public static AbstractProtocolHandler initServerHandlerToChannelContext(ByteBuffer buffer, ChannelContext channelContext) {
        IProtocol protocol = ImKit.protocol(buffer, channelContext);
        for (Entry<String, AbstractProtocolHandler> entry : serverHandlers.entrySet()) {
            AbstractProtocolHandler protocolHandler = entry.getValue();
            String protocolName = protocolHandler.protocol().name();
            try {
                if (protocol != null && protocol.name().equals(protocolName)) {
                    ImSessionContext sessionContext = (ImSessionContext) channelContext.getAttribute();
                    sessionContext.setProtocolHandler(protocolHandler);
                    channelContext.setAttribute(sessionContext);
                    return protocolHandler;
                }
            } catch (Throwable e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public static <T> T getServerHandler(String name, Class<T> clazz) {
        AbstractProtocolHandler serverHandler = serverHandlers.get(name);
        if (Objects.isNull(serverHandler)) {
            return null;
        }
        return (T) serverHandler;
    }

    public static void init(ImConfig imConfig) {
        for (Entry<String, AbstractProtocolHandler> entry : serverHandlers.entrySet()) {
            try {
                entry.getValue().init(imConfig);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
