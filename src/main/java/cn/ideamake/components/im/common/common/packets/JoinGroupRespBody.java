/**
 *
 */
package cn.ideamake.components.im.common.common.packets;

import cn.ideamake.components.im.common.common.packets.JoinGroupResult;
import cn.ideamake.components.im.common.common.packets.RespBody;

/**
 * 版本: [1.0]
 * 功能说明: 加入群组响应
 * 作者: WChao 创建时间: 2017年7月26日 下午5:09:20
 */
public class JoinGroupRespBody extends RespBody {

    private static final long serialVersionUID = 6635620192752369689L;
    public JoinGroupResult result;
    public String group;

    public JoinGroupResult getResult() {
        return result;
    }

    public void setResult(JoinGroupResult result) {
        this.result = result;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
