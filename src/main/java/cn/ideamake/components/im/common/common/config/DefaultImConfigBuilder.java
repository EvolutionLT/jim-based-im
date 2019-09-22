/**
 *
 */
package cn.ideamake.components.im.common.common.config;

import cn.ideamake.components.im.common.common.config.ImConfigBuilder;
import cn.ideamake.components.im.common.common.http.HttpConfig;
import cn.ideamake.components.im.common.common.ws.WsServerConfig;

/**
 * @author WChao
 *
 */
public class DefaultImConfigBuilder extends ImConfigBuilder {

    /* (non-Javadoc)
     * @see cn.ideamake.components.im.common.common.config.ImConfigBuilder#configHttp(cn.ideamake.components.im.common.common.http.HttpConfig)
     */
    @Override
    public ImConfigBuilder configHttp(HttpConfig httpConfig) {
        // TODO Auto-generated method stub
        return this;
    }

    /* (non-Javadoc)
     * @see cn.ideamake.components.im.common.common.config.ImConfigBuilder#configWs(cn.ideamake.components.im.common.common.ws.WsServerConfig)
     */
    @Override
    public ImConfigBuilder configWs(WsServerConfig wsServerConfig) {
        // TODO Auto-generated method stub
        return this;
    }

}
