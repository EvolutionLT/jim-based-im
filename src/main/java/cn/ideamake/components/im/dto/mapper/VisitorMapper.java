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
public interface VisitorMapper extends BaseMapper<Visitor> {

    Boolean userIsValid(@Param("userId") String id, @Param("token") String token);

    /**
    * @description: 查询访客信息
    * @param: [openId]
    * @return: cn.ideamake.components.im.pojo.entity.Visitor
    * @author: apollo
    * @date: 2019-09-26
    */
    Visitor selectByOpenId(@Param("openId") String openId);

}
