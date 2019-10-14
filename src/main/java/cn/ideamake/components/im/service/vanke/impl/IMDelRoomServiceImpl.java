package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.components.im.dto.mapper.IMDelRoomMapper;

import cn.ideamake.components.im.pojo.entity.IMDelRoom;
import cn.ideamake.components.im.service.vanke.IMDelRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evolution
 * @since 2019-08-31
 */
@Service
public class IMDelRoomServiceImpl extends ServiceImpl<IMDelRoomMapper, IMDelRoom> implements IMDelRoomService {
    @Autowired
    private IMDelRoomMapper delRoomMapper;
    @Override
    public IMDelRoom getDelRoomInfo(String roomId) {
        return delRoomMapper.getDelRoomInfo(roomId);
    }

    @Override
    public int deleteDelRoomInfo(String  roomId) {
        return delRoomMapper.deleteDelRoomInfo(roomId);
    }
}
