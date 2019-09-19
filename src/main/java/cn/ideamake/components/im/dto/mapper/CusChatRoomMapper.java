package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.CusChatRoom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CusChatRoomMapper extends BaseMapper<CusChatRoom> {

    CusChatRoom selectByUniqueCode(String code);
}
