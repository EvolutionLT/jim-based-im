package cn.ideamake.components.im.service.vanke;


import cn.ideamake.components.im.common.common.packets.LoginReqBody;
import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMUsers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evolution
 * @since 2019-07-02
 */
@Repository
public interface IMUserService extends IService<IMUsers> {
    /**
     * 根据ID查询用户信息
     * @param id
     * @return
     */
    public IMUsers getUserInfo(String id);


    public int addUser(LoginReqBody loginReqBody);

    public List<IMUsers> getAllUser(ChatUserListDTO chatUserListQuery);


}
