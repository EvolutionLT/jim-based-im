/**
 * 
 */
package cn.ideamake.components.im.common.common.packets;

import java.util.List;

/**
 * 版本: [1.0]
 * 功能说明:
 * 作者: WChao 创建时间: 2017年9月21日 下午1:54:04
 */
public class Group extends Message {
	
	private static final long serialVersionUID = -3817755433171220952L;
	private String groupId;
	private String ownerId;
	private String name;//群组名称;
	private String avatar;//群组头像;
	private Integer online;//在线人数;
	private List<User> users;//组用户;

	public Group(){}
	public Group(String groupId, String name){
		this.groupId = groupId;
		this.name = name;
	}
	public Group(String ownerId, String groupId, String name){
		this.ownerId = ownerId;
		this.groupId = groupId;
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Integer getOnline() {
		return online;
	}
	public void setOnline(Integer online) {
		this.online = online;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
