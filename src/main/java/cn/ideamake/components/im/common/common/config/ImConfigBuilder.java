/**
 *
 */
package cn.ideamake.components.im.common.common.config;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.cluster.ImCluster;
import cn.ideamake.components.im.common.common.http.HttpConfig;
import cn.ideamake.components.im.common.common.message.MessageHelper;
import cn.ideamake.components.im.common.common.ws.WsServerConfig;
import org.tio.core.GroupContext;
import org.tio.core.intf.GroupListener;
import org.tio.core.ssl.SslConfig;

/**
 * @author WChao
 * 2018/08/26
 */
public abstract class ImConfigBuilder implements Config.Builder {

    protected ImConfig conf;

    public ImConfigBuilder() {
        this.conf = new ImConfig();
    }

    public abstract ImConfigBuilder configHttp(HttpConfig httpConfig);

    public abstract ImConfigBuilder configWs(WsServerConfig wsServerConfig);

    public ImConfigBuilder setBindIp(String bindIp) {
        this.conf.bindIp = bindIp;
        return this;
    }

    public ImConfigBuilder setBindPort(Integer bindPort) {
        this.conf.bindPort = bindPort;
        return this;
    }

    public ImConfigBuilder setHeartbeatTimeout(long heartbeatTimeout) {
        this.conf.heartbeatTimeout = heartbeatTimeout;
        return this;
    }

    public ImConfigBuilder setGroupContext(GroupContext groupContext) {
        this.conf.groupContext = groupContext;
        return this;
    }

    public ImConfigBuilder setImGroupListener(GroupListener imGroupListener) {
        this.conf.imGroupListener = imGroupListener;
        return this;
    }

    public ImConfigBuilder setMessageHelper(MessageHelper messageHelper) {
        this.conf.messageHelper = messageHelper;
        return this;
    }

    public ImConfigBuilder setIsStore(String isStore) {
        this.conf.isStore = isStore;
        return this;
    }

    public ImConfigBuilder setIsCluster(String isCluster) {
        this.conf.isCluster = isCluster;
        return this;
    }

    public ImConfigBuilder setIsSSL(String isSSL) {
        this.conf.isSSL = isSSL;
        return this;
    }

    public ImConfigBuilder setSslConfig(SslConfig sslConfig) {
        this.conf.sslConfig = sslConfig;
        return this;
    }

    public ImConfigBuilder setCluster(ImCluster cluster) {
        this.conf.cluster = cluster;
        return this;
    }

    public ImConfigBuilder setReadBufferSize(long readBufferSize) {
        this.conf.readBufferSize = readBufferSize;
        return this;
    }

    @Override
    public ImConfig build() {
        this.configHttp(conf.getHttpConfig());
        this.configWs(conf.getWsServerConfig());
        return conf;
    }
}
