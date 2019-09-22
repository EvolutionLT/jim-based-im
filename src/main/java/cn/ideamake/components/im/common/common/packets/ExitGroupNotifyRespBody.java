/**
 *
 */
package cn.ideamake.components.im.common.common.packets;

import cn.ideamake.components.im.common.common.packets.Message;
import cn.ideamake.components.im.common.common.packets.User;

/**
 * 版本: [1.0]
 * 功能说明: 退出群组通知消息体
 * 作者: WChao 创建时间: 2017年7月26日 下午5:15:18
 */
public class ExitGroupNotifyRespBody extends Message {

    private static final long serialVersionUID = 3680734574052114902L;
    private User user;
    private String group;

    public User getUser() {
        return user;
    }

    public cn.ideamake.components.im.common.common.packets.ExitGroupNotifyRespBody setUser(User user) {
        this.user = user;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public cn.ideamake.components.im.common.common.packets.ExitGroupNotifyRespBody setGroup(String group) {
        this.group = group;
        return this;
    }
}
