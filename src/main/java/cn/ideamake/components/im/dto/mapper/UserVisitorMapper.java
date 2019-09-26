package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.UserVisitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ideamake
 * @since 2019-07-10
 */
public interface UserVisitorMapper extends BaseMapper<UserVisitor> {
    /**
    * @description: 查询置业顾问id
    * @param: [visitorCode, projectCode]
    * @return: java.lang.String
    * @author: apollo
    * @date: 2019-09-25
    */
    String selectUserId(@Param("visitorCode") String visitorCode, @Param("projectCode") String projectCode);
}