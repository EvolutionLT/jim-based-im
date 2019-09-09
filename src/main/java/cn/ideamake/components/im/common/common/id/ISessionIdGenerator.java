package cn.ideamake.components.im.common.common.id;

import cn.ideamake.components.im.common.common.http.HttpConfig;

/**
 * @author wchao
 * 2017年8月15日 上午10:49:58
 */
public interface ISessionIdGenerator {

	/**
	 *
	 * @return
	 * @author wchao
	 */
	String sessionId(HttpConfig httpConfig);

}
