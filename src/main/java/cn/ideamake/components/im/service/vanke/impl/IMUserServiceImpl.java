package cn.ideamake.components.im.service.vanke.impl;


import cn.ideamake.components.im.common.common.packets.LoginReqBody;
import cn.ideamake.components.im.dto.mapper.IMUserMapper;
import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMUsers;
import cn.ideamake.components.im.service.vanke.IMUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evolution
 * @since 2019-07-02
 */
@Service
public class IMUserServiceImpl extends ServiceImpl<IMUserMapper, IMUsers> implements IMUserService {


    @Autowired
    private IMUserMapper userMapper;

    @Override
    public IMUsers getUserInfo(String id) {
        return userMapper.getUserInfo(id);
    }

    @Override
    public int addUser(LoginReqBody loginReqBody) {
        IMUsers user= new IMUsers();
        user.setUuid(loginReqBody.getUserId());
        user.setNick(loginReqBody.getNick());
        user.setAvatar(loginReqBody.getAvatar());
        user.setChannel(loginReqBody.getChannel());
        user.setCapacity(loginReqBody.getCapacity());
        user.setCreateAt(LocalDateTime.now());
        return userMapper.insert(user);
    }

    @Override
    public List<IMUsers> getAllUser(ChatUserListDTO chatUserListQuery) {
        return userMapper.getAllUser(chatUserListQuery);
    }


}
