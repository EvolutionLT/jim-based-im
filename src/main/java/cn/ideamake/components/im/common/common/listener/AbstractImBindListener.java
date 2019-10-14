/**
 *
 */
package cn.ideamake.components.im.common.common.listener;

import cn.ideamake.components.im.common.common.ImConfig;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.listener.ImBindListener;

/**
 * @author WChao
 * 2018/08/26
 */
public abstract class AbstractImBindListener implements ImBindListener, ImConst {

    protected ImConfig imConfig;

    public ImConfig getImConfig() {
        return imConfig;
    }

    public void setImConfig(ImConfig imConfig) {
        this.imConfig = imConfig;
    }
}
