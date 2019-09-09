/**
 * 
 */
package cn.ideamake.components.im.common.common.message;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.message.MessageHelper;

/**
 * @author HP
 *
 */
public abstract class AbstractMessageHelper implements MessageHelper, ImConst {
	protected ImConfig imConfig;

	public ImConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(ImConfig imConfig) {
		this.imConfig = imConfig;
	}
}
