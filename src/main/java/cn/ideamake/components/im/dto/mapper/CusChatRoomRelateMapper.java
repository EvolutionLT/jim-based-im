package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.CusChatMember;
import cn.ideamake.components.im.pojo.entity.CusChatRoomRelate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CusChatRoomRelateMapper extends BaseMapper<CusChatRoomRelate>{
    /**
    * @description: 修改所有数据为下线状态
    * @param: [userId, status]
    * @return: void
    * @author: apollo
    * @date: 2019-09-18
    */
    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    List<CusChatRoomRelate> selectReleates(String userId);
}
