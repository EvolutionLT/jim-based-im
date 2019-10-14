package cn.ideamake.components.im.common.server.util;

import cn.ideamake.components.im.common.common.http.GroupContextKey;
import cn.ideamake.components.im.common.common.http.HttpConfig;
import cn.ideamake.components.im.common.common.http.HttpRequest;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

/**
 * @author WChao
 * 2017年8月18日 下午5:47:00
 */
public class HttpServerUtils {
    /**
     * @param request
     * @return
     * @author WChao
     */
    public static HttpConfig getHttpConfig(HttpRequest request) {
        ChannelContext channelContext = request.getChannelContext();
        GroupContext groupContext = channelContext.getGroupContext();
        HttpConfig httpConfig = (HttpConfig) groupContext.getAttribute(GroupContextKey.HTTP_SERVER_CONFIG);
        return httpConfig;
    }

    /**
     * @param args
     * @author WChao
     */
    public static void main(String[] args) {

    }

    /**
     * @author WChao
     */
    public HttpServerUtils() {
    }
}
