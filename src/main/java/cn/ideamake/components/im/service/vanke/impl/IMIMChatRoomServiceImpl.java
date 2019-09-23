package cn.ideamake.components.im.service.vanke.impl;


import cn.ideamake.common.response.IdeamakePage;
import cn.ideamake.common.response.Result;

import cn.ideamake.components.im.dto.mapper.IMChatRecordMapper;
import cn.ideamake.components.im.dto.mapper.IMChatRoomMapper;
import cn.ideamake.components.im.dto.mapper.IMDelRoomMapper;
import cn.ideamake.components.im.pojo.dto.ChatMsgListDTO;
import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMChatRoom;
import cn.ideamake.components.im.pojo.entity.IMDelRoom;
import cn.ideamake.components.im.pojo.vo.ChatMsgListVO;
import cn.ideamake.components.im.pojo.vo.ChatUserListVO;
import cn.ideamake.components.im.service.vanke.IMChatRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evolution
 * @since 2019-07-07
 */
@Service
public class IMIMChatRoomServiceImpl extends ServiceImpl<IMChatRoomMapper, IMChatRoom> implements IMChatRoomService {

    @Autowired
    private IMChatRoomMapper chatRoomMapper;
    @Autowired
    private IMChatRecordMapper chatRecordMapper;
    //@Autowired
  //  private RedisUtil redisUtil;
    @Autowired
    private IMDelRoomMapper delRoomMapper;


    @Override
    public Result insertChatRoom(IMChatRoom chatRoom) {
       String chatRoomInfo =chatRoomMapper.getIsRoom(chatRoom.getCreateUserId(),chatRoom.getUserId());
        if(chatRoomInfo!=null){
            return Result.ok(chatRoomInfo);
        }else{
            int result =chatRoomMapper.insert(chatRoom);
            return Result.ok(chatRoom.getId());
        }
    }

    @Override
    public Result getAllChatUserList(ChatUserListDTO chatUserListQuery) {
        Result apiResponse =Result.ok();
        IdeamakePage ideamakePage = new IdeamakePage(chatUserListQuery.getPage(),chatUserListQuery.getLimit());
        List<ChatUserListVO> list =chatRoomMapper.getAllChatUserList(ideamakePage,chatUserListQuery);
        ChatMsgListDTO chatMsgListQuery = new ChatMsgListDTO();
        //遍历出来查询最近消息
        for(ChatUserListVO chatUserListVO : list){
            chatMsgListQuery.setLimit(1);
            chatMsgListQuery.setPage(1);
            ideamakePage= new IdeamakePage(chatMsgListQuery.getPage(),chatMsgListQuery.getLimit());
            chatMsgListQuery.setRoomId(chatUserListVO.getRoomId());
            List<ChatMsgListVO> chatMsgListVO= chatRecordMapper.getMsgList(ideamakePage,chatMsgListQuery);
            if(chatMsgListVO.size()>0){
                chatUserListVO.setMsgContent(chatMsgListVO.get(0).getMsgContent());
                chatUserListVO.setDate(chatMsgListVO.get(0).getDates());

           }
            //查询当前房间是否有未读信息
            int num =chatRecordMapper.getUserMsgNotRead(chatUserListVO.getRoomId(),"1",chatUserListVO.getUserId());
                chatUserListVO.setNotRead(num+"");
            //从redis中取出数据看是否在线
           // chatUserListVO.setIsOnline(redisUtil.get(BasicConstants.IMUSERKEY+chatUserListVO.getUserId()));
        }

        ideamakePage.setList(list);
        //ideamakePage.setDesc("date");
        apiResponse.setData(ideamakePage);
        return apiResponse;
    }


    @Override
    public Result  getIsRoom(String id,String user_id) {
      String chatRoom =chatRoomMapper.getIsRoom(id,user_id);
        return Result.ok(chatRoom);
    }

    @Override
    public Result deleteRoom(ChatUserListDTO chatUserListQuery) {
        //  int result =chatRoomMapper.deleteRoom(chatUserListQuery.getRoomId());
       // chatRecordMapper.deletechatRecord(chatUserListQuery.getRoomId());
        IMDelRoom delRoom = new IMDelRoom();
        delRoom.setRoomId(chatUserListQuery.getRoomId());
        delRoom.setCreateUser(chatUserListQuery.getFrom());
        delRoom.setCreateAt(LocalDateTime.now());
        int result = delRoomMapper.insert(delRoom);

        return Result.ok(result);
    }
}
