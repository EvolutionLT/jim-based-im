/**
 *
 */
package cn.ideamake.components.im.service.impl;

import cn.ideamake.components.im.common.common.packets.Group;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户id;
     */
    private String id;
    /**
     * user nick
     */
    private String nick;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 在线状态(online、offline)
     */
    private String status;
    /**
     * 个性签名;
     */
    private String sign;
    /**
     * 用户所属终端;
     */
    private String terminal;
    /**
     * 好友列表;
     */
    private List<Group> friends;
    /**
     * 群组列表;
     */
    private List<Group> groups;
    /**
     * 扩展预留字段;
     */
    private JSONObject extras;

    public UserInfo() {
    }

    public UserInfo(String id, String nick) {
        this.id = id;
        this.nick = nick;
    }
//	public String getNick() {
//		return nick;
//	}
//	public void setNick(String nick) {
//		this.nick = nick;
//	}
//	public String getAvatar() {
//		return avatar;
//	}
//	public void setAvatar(String avatar) {
//		this.avatar = avatar;
//	}
//
//	public List<Group> getGroups() {
//		return groups;
//	}
//	public void setGroups(List<Group> groups) {
//		this.groups = groups;
//	}
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
//	public String getSign() {
//		return sign;
//	}
//	public void setSign(String sign) {
//		this.sign = sign;
//	}
//	public List<Group> getFriends() {
//		return friends;
//	}
//	public void setFriends(List<Group> friends) {
//		this.friends = friends;
//	}
//	public String getTerminal() {
//		return terminal;
//	}
//	public void setTerminal(String terminal) {
//		this.terminal = terminal;
//	}
//	public JSONObject getExtras() {
//		return extras;
//	}
//	public void setExtras(JSONObject extras) {
//		this.extras = extras;
//	}

}
