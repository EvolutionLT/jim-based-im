/**
 * 
 */
package cn.ideamake.components.im.common.common;

import cn.ideamake.components.im.common.common.config.Config;
import cn.ideamake.components.im.common.common.http.HttpConfig;
import cn.ideamake.components.im.common.common.ws.WsServerConfig;

/**
 * @author WChao
 *
 */
public class ImConfig extends Config {
	
	/**
	 * http相关配置;
	 */
	private HttpConfig httpConfig;
	/**
	 * WebSocket相关配置;
	 */
	private WsServerConfig wsServerConfig;
	
	public ImConfig() {
		this.httpConfig = new HttpConfig();
		this.wsServerConfig = new WsServerConfig();
	}
	
	public ImConfig(String bindIp,Integer bindPort){
		this.bindIp = bindIp;
		this.bindPort = bindPort;
		this.httpConfig = new HttpConfig();
		this.wsServerConfig = new WsServerConfig();
	}

	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public WsServerConfig getWsServerConfig() {
		return wsServerConfig;
	}

	public void setWsServerConfig(WsServerConfig wsServerConfig) {
		this.wsServerConfig = wsServerConfig;
	}
	
}
