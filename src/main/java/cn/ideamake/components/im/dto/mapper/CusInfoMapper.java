package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.CusInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 客服表 Mapper 接口
 * </p>
 *
 * @author ideamake
 * @since 2019-08-15
 */
public interface CusInfoMapper extends BaseMapper<CusInfo> {

    /**
    * @description: 根据userId和token查询
    * @param: [id, token]
    * @return: cn.ideamake.components.im.pojo.entity.CusInfo
    * @author: apollo
    * @date: 2019-09-18
    */
    CusInfo selectByIdAndToken(@Param("userId") String id, @Param("token") String token);

    /**
    * @description: 校验用户是否存在
    * @param: [id, token]
    * @return: java.lang.Boolean
    * @author: apollo
    * @date: 2019-09-19
    */
    Boolean userIsValid(@Param("userId") String id, @Param("token") String token);
}
