package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.UserVisitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ideamake
 * @since 2019-07-10
 */
public interface UserVisitorMapper extends BaseMapper<UserVisitor> {
    String selectUserId(String visitorCode);
}