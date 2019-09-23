package cn.ideamake.components.im.service.vanke;

import cn.ideamake.components.im.pojo.entity.IMDelRoom;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evolution
 * @since 2019-08-31
 */
public interface IMDelRoomService extends IService<IMDelRoom> {

    IMDelRoom getDelRoomInfo(String roomId);
    int  deleteDelRoomInfo(String roomId);

}
