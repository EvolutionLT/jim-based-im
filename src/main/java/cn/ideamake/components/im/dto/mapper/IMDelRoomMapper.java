package cn.ideamake.components.im.dto.mapper;

import cn.ideamake.components.im.pojo.entity.IMDelRoom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author evolution
 * @since 2019-08-31
 */
public interface IMDelRoomMapper extends BaseMapper<IMDelRoom> {

    IMDelRoom getDelRoomInfo(@Param("roomId") String roomId);
    int  deleteDelRoomInfo(@Param("roomId") String roomId);

}
