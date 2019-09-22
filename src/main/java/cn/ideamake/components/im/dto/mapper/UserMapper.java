package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.common.response.IdeamakePage;
import cn.ideamake.components.im.pojo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ideamake
 * @since 2019-07-10
 */
public interface UserMapper extends BaseMapper<User> {
    Boolean userIsValid(@Param("userId") String id, @Param("token") String token);
}