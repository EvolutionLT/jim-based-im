package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.CusChatMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface CusChatMemberMapper extends BaseMapper<CusChatMember>{
    /**
    * @description: 根据聊天成员userId查询
    * @param: [userId]
    * @return: cn.ideamake.components.im.pojo.entity.CusChatMember
    * @author: apollo
    * @date: 2019-09-18
    */
    CusChatMember selectByUserId(String userId);

    /**
    * @description: 修改成员状态
    * @param: [userId, status]
    * @return: void
    * @author: apollo
    * @date: 2019-09-18
    */
    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
    * @description: 查询空闲客服
    * @return: java.util.List<cn.ideamake.components.im.pojo.entity.CusChatMember>
    * @author: apollo
    * @date: 2019-09-19
    */
    List<CusChatMember> selectCustomer();
}
