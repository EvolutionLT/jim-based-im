/**
 *
 */
package cn.ideamake.components.im.common.common.cluster;

import cn.ideamake.components.im.common.common.cluster.ICluster;
import cn.ideamake.components.im.common.common.cluster.ImClusterConfig;

/**
 *
 * @author WChao
 *
 */
public abstract class ImCluster implements ICluster {

    protected ImClusterConfig clusterConfig;

    public ImCluster(ImClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig;
    }

    public ImClusterConfig getClusterConfig() {
        return clusterConfig;
    }
}
