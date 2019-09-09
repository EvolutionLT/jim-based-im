/**
 * 
 */
package cn.ideamake.components.im.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.ideamake.components.im.common.common.id.impl.UUIDSessionIdGenerator;
import cn.ideamake.components.im.service.PeriodService;
import org.apache.commons.lang3.StringUtils;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.ImPacket;
import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.ImStatus;
import cn.ideamake.components.im.common.common.http.HttpConst;
import cn.ideamake.components.im.common.common.packets.*;
import cn.ideamake.components.im.common.common.utils.JsonKit;
import cn.ideamake.components.im.common.common.utils.Md5;
import cn.ideamake.components.im.common.server.command.CommandManager;
import cn.ideamake.components.im.common.server.command.handler.JoinGroupReqHandler;
import cn.ideamake.components.im.common.server.command.handler.processor.login.LoginCmdProcessor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

import java.util.*;


public class LoginServiceProcessor implements LoginCmdProcessor {

	PeriodService periodService = new PeriodServiceImpl();

//	private Logger logger = LoggerFactory.getLogger(LoginServiceProcessor.class);

	public static final Map<String, User> tokenMap = new HashMap<>();

	private static String[] familyName = new String[] { "谭", "刘", "张", "李", "胡", "沈", "朱", "钱", "王", "伍", "赵", "孙", "吕", "马", "秦", "毛", "成", "梅", "黄", "郭", "杨", "季", "童", "习", "郑",
			"吴", "周", "蒋", "卫", "尤", "何", "魏", "章", "郎", " 唐", "汤", "苗", "孔", "鲁", "韦", "任", "袁", "贺", "狄朱" };

	private static String[] secondName = new String[] { "艺昕", "红薯", "明远", "天蓬", "三丰", "德华", "歌", "佳", "乐", "天", "燕子", "子牛", "海", "燕", "花", "娟", "冰冰", "丽娅", "大为", "无为", "渔民", "大赋",
			"明", "远平", "克弱", "亦菲", "靓颖", "富城", "岳", "先觉", "牛", "阿狗", "阿猫", "辰", "蝴蝶", "文化", "冲之", "悟空", "行者", "悟净", "悟能", "观", "音", "乐天", "耀扬", "伊健", "炅", "娜", "春花", "秋香", "春香",
			"大为", "如来", "佛祖", "科比", "罗斯", "詹姆屎", "科神", "科蜜", "库里", "卡特", "麦迪", "乔丹", "魔术师", "加索尔", "法码尔", "南斯", "伊哥", "杜兰特", "保罗", "杭州", "爱湘", "湘湘", "昕", "函", "鬼谷子", "膑", "荡",
			"子家", "德利优视", "五方会谈", "来电话了", "T-IO", "Talent" ,"轨迹","超"};
	
	/**
	 * 根据用户名和密码获取用户
	 * @param loginname
	 * @param password
	 * @return
	 * @author: WChao
	 */
	public User getUser(String loginname, String password) {
		String text = loginname+password;
		String key = ImConst.AUTH_KEY;
		String token = Md5.sign(text, key, HttpConst.CHARSET_NAME);
		User user = getUser(token);
		user.setId(loginname);
		return user;
	}
	/**
	 * 根据token获取用户信息
	 * @param token
	 * @return
	 * @author: WChao
	 */
	public User getUser(String token) {
		//demo中用map，生产环境需要用cache
		User user = periodService.getUserInfoById(token);
		if (user == null) {
			user = new User();
			user.setId(UUIDSessionIdGenerator.instance.sessionId(null));
			user.setNick(familyName[RandomUtil.randomInt(0, familyName.length - 1)] + secondName[RandomUtil.randomInt(0, secondName.length - 1)]);
			
			user.setGroups(initGroups(user));
			user.setFriends(initFriends(user));
			user.setAvatar(nextImg());
			
			if (tokenMap.size() > 10000) {
				tokenMap.clear();
			}
			tokenMap.put(token, user);
		}
		return user;
	}
	
	public List<Group> initGroups(User user){
		//模拟的群组;正式根据业务去查数据库或者缓存;
		List<Group> groups = new ArrayList<Group>();
		groups.add(new Group("100","J-IM朋友圈"));
		return groups;
	}
	public List<Group> initFriends(User user){
		List<Group> friends = new ArrayList<Group>();
		Group myFriend = new Group("1","我的好友");
		List<User> myFriendGroupUsers = new ArrayList<User>();
		User user1 = new User();

		//此处初始化用户,通过登录的用户id从真实数据库中查询，暂时打算远程调用业务端接口，封装一个http客户端#TODO
		user1.setId(UUIDSessionIdGenerator.instance.sessionId(null));
		user1.setNick(familyName[RandomUtil.randomInt(0, familyName.length - 1)] + secondName[RandomUtil.randomInt(0, secondName.length - 1)]);
		user1.setAvatar(nextImg());
		myFriendGroupUsers.add(user1);
		myFriend.setUsers(myFriendGroupUsers);
		friends.add(myFriend);
		return friends;
	}
	
	public String nextImg() {
		return ImgMnService.nextImg();
	}

	public String newToken() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * doLogin方法注意：J-IM登陆命令是根据user是否为空判断是否登陆成功,
	 * 
	 * 当登陆失败时设置user属性需要为空，相反登陆成功user属性是必须非空的;
	 */
	@Override
	public LoginRespBody doLogin(LoginReqBody loginReqBody , ChannelContext channelContext) {
//		return null;
		String loginname = loginReqBody.getUserId();
		String password = loginReqBody.getPassword();
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		String handshakeToken = imSessionContext.getToken();
		User user;
		LoginRespBody loginRespBody;
		if (!StringUtils.isBlank(handshakeToken)) {
			user = this.getUser(handshakeToken);
		}else{
			user = this.getUser(loginname, password);
		}
		if(user == null){
			loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10008);
		}else{
			loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10007,user);
		}
		return loginRespBody;
	}

	@Override
	public void onSuccess(ChannelContext channelContext) {
//		logger.info("登录成功回调方法");
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		User user = imSessionContext.getClient().getUser();
		if(user.getGroups() != null){
			for(Group group : user.getGroups()){//发送加入群组通知
				ImPacket groupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ, JsonKit.toJsonBytes(group));
				try {
					JoinGroupReqHandler joinGroupReqHandler = CommandManager.getCommand(Command.COMMAND_JOIN_GROUP_REQ, JoinGroupReqHandler.class);
					joinGroupReqHandler.joinGroupNotify(groupPacket, channelContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean isProtocol(ChannelContext channelContext) {
		 
		return true;
	}

	@Override
	public String name() {
		
		return "default";
	}
}
