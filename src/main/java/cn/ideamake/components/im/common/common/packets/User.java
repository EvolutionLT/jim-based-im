/**
 * 
 */
package cn.ideamake.components.im.common.common.packets;

import com.alibaba.fastjson.JSONObject;
import cn.ideamake.components.im.common.common.packets.Group;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id;
	 */
	private String id;

	/**
	 * 身份类型 0=客服 1=访客 2=置业顾问
	 */
	private Integer type;

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

	public User(){}
	public User(String id , String nick){
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
