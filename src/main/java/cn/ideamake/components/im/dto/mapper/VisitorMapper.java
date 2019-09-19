package cn.ideamake.components.im.dto.mapper;


import cn.ideamake.components.im.pojo.entity.Visitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 访客表 Mapper 接口
 * </p>
 *
 * @author ideamake
 * @since 2019-07-11
 */
@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {

    Boolean userIsValid(@Param("userId") String id, @Param("token") String token);

}
