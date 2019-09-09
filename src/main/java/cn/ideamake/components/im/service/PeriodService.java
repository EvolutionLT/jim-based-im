package cn.ideamake.components.im.service;

import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.pojo.dto.LoginDTO;

public interface PeriodService {
    /**
     * 获取用户基本界面初始化信息
     * @param userId
     * @return
     */
    User getUserInfoById(String userId);


    /**
     * 通过token获取用户
     * @param token
     * @return
     */
    User getUserInfoByToken(String token);
    /**
     * 用户通过用户名密码或者其他协商信息获取im下发token
     * 1.用户携带验证参数调用im-server接口
     * 2.im-server携带登录信息向授权服务（目前是在应用服务）做校验
     * 3.校验成功后将授权token返回到请求接口，并存储该token
     * 4.前端携带该token和用户id做websocket连接，验证通过用户上线
     * @param loginDTO
     * @return
     */
    String loginInfoToToken(LoginDTO loginDTO);

    /**
     * 更新用户基本信息,备用
     * @param user
     * @return
     */
    boolean updateUserInfo(User user);
}
