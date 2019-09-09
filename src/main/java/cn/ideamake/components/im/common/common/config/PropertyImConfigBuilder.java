/**
 * 
 */
package cn.ideamake.components.im.common.common.config;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.config.ImConfigBuilder;
import cn.ideamake.components.im.common.common.http.HttpConfig;
import cn.ideamake.components.im.common.common.utils.PropUtil;
import cn.ideamake.components.im.common.common.ws.WsServerConfig;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author WChao
 * 2018/08/26
 */
@Slf4j
public class PropertyImConfigBuilder extends ImConfigBuilder {
	
	public PropertyImConfigBuilder(String file) {
		PropUtil.use(file);
	}
	
	@Override
	public ImConfigBuilder configHttp(HttpConfig httpConfig) {
		//html/css/js等的根目录，支持classpath:，也支持绝对路径
		String pageRoot = PropUtil.get("jim.http.page");
		//j-im mvc需要扫描的根目录包
		String[] scanPackages = PropUtil.get("jim.http.scan.packages").split(",");
		httpConfig.setBindPort((PropUtil.getInt("jim.port")));
		//设置web访问路径;
		httpConfig.setPageRoot(pageRoot);
		//不缓存资源;
		httpConfig.setMaxLiveTimeOfStaticRes(PropUtil.getInt("jim.http.max.live.time"));
		//设置j-im mvc扫描目录;
		httpConfig.setScanPackages(scanPackages);
		return this;
	}

	@Override
	public ImConfigBuilder configWs(WsServerConfig wsServerConfig) {
		
		return this;
	}

	@Override
	public ImConfig build() {
		super.build();
		InetAddress localHost = null;
		try {
			localHost = Inet4Address.getLocalHost();
		} catch (UnknownHostException e) {
			log.error(e.getMessage(),e);
		}
		String ip = localHost.getHostAddress();
//		this.setBindIp(PropUtil.get("jim.bind.ip"));
		this.setBindIp(ip);
		this.setBindPort(PropUtil.getInt("jim.port"));
		this.setHeartbeatTimeout(PropUtil.getLong("jim.heartbeat.timeout"));
		this.setIsStore(PropUtil.get("jim.store"));
		this.setIsCluster(PropUtil.get("jim.cluster"));
		return conf;
	}
}
