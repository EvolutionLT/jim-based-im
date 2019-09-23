package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.CusInfo;
import cn.ideamake.components.im.pojo.entity.CusVisitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 客服表 Mapper 接口
 * </p>
 *
 * @author ideamake
 * @since 2019-08-15
 */
public interface CusVisitorMapper extends BaseMapper<CusVisitor> {
    /**
    * @description: 根据访客Id查询客服信息
    * @param: [visitorId]
    * @return: cn.ideamake.components.im.pojo.entity.CusVisitor
    * @author: apollo
    * @date: 2019-09-23
    */
    CusInfo selectCusInfoByVisitor(String visitorId);
}
