package cn.ideamake.components.im.service.vanke.impl;


import cn.ideamake.common.response.IdeamakePage;
import cn.ideamake.common.response.Result;

import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.common.utils.BasicConstants;
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
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if(chatUserListQuery.getUuid()!=null && !chatUserListQuery.getUuid().equals("undefined")){
            IdeamakePage ideamakePage = new IdeamakePage(chatUserListQuery.getPage(),chatUserListQuery.getLimit());
            List<ChatUserListVO> list = new ArrayList<>();
            list=chatRoomMapper.getAllChatUserList(ideamakePage,chatUserListQuery);
            ChatMsgListDTO chatMsgListQuery = new ChatMsgListDTO();
            //遍历出来查询最近消息
            for(ChatUserListVO chatUserListVO : list){
                ideamakePage= new IdeamakePage(1,1);
                chatMsgListQuery.setRoomId(chatUserListVO.getRoomId());
                List<ChatMsgListVO> chatMsgListVO= chatRecordMapper.getMsgList(ideamakePage,chatMsgListQuery);
                if(chatMsgListVO.size()>0){
                    chatUserListVO.setMsgContent(chatMsgListVO.get(0).getMsgContent());
                    chatUserListVO.setDate(chatMsgListVO.get(0).getDates());
                }
                //查询当前房间是否有未读信息
                int num =chatRecordMapper.getUserMsgNotRead(chatUserListVO.getRoomId(),"1",chatUserListQuery.getUuid());
                chatUserListVO.setNotRead(num+"");
                chatUserListVO.setUserType(2);
                //从redis中取出数据看是否在线
                //chatUserListVO.setIsOnline(redisUtil.get(BasicConstants.IMUSERKEY+chatUserListVO.getUserId()));
            }
            List<ChatUserListVO> lista = new ArrayList<>();
            //查询用户已绑定客服
            RMapCache<String, User> friendsOfSender = RedissonTemplate.me().getRedissonClient().getMapCache(Constants.USER.PREFIX + ":" + chatUserListQuery.getUuid() + ":" + Constants.USER.FRIENDS);
           if(friendsOfSender.size()>0){
               ChatUserListVO cusInfo=new ChatUserListVO();
               for (String key :friendsOfSender.keySet()){
                   User user = friendsOfSender.get(key);
                   cusInfo.setAvatar(user.getAvatar());
                   cusInfo.setUserType(user.getType()==null? 0:user.getType());
                   cusInfo.setUserId(user.getId());
                   cusInfo.setNick(user.getNick());
                   cusInfo.setNotRead("0");
                   lista.add(cusInfo);
               }

           }
           ideamakePage.setList(lista);
            //ideamakePage.setDesc("date");
            apiResponse.setData(ideamakePage);
        }

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
