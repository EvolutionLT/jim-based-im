package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.common.common.packets.LoginReqBody;
import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMUsers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author evolution
 * @since 2019-07-02
 */

public interface IMUserMapper extends BaseMapper<IMUsers> {
    /**
     * 根据ID查询用户信息
     * @param id
     * @return
     */
     IMUsers getUserInfo(@Param("id") String id);

    /**
     * 查询所有用户列表
     * @return
     */
     List getUserList();

    /**
     * 修改用户咨询人数
     * @param service_count
     * @param id
     */
    void updateServiceCount(@Param("serviceCount") String service_count, @Param("id") String id);

    /**
     * 修改满意度
     * @param degree
     * @param id
     */
    void updateUserDegree(@Param("degree") int degree, @Param("id") String id);

    List<IMUsers> getAllUser(@Param("query") ChatUserListDTO chatUserListQuery);

    /**
     * 修改用户信息
     */
    void updateUserInfo(@Param("query") LoginReqBody loginReqBody);

}
