package cn.ideamake.components.im.durables.channel;

import cn.ideamake.common.exception.BusinessException;
import cn.ideamake.common.util.LocalDateTimeUtils;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.dto.mapper.IMChatRecordMapper;
import cn.ideamake.components.im.dto.mapper.IMChatRoomMapper;
import cn.ideamake.components.im.dto.mapper.IMDelRoomMapper;
import cn.ideamake.components.im.durables.DataCrudStrategy;
import cn.ideamake.components.im.durables.DataWay;
import cn.ideamake.components.im.pojo.entity.IMChatRecord;
import cn.ideamake.components.im.pojo.entity.IMChatRoom;
import cn.ideamake.components.im.pojo.entity.IMDelRoom;
import cn.ideamake.components.im.pojo.vo.ChatUserListVO;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author evolution
 * @title: MysqlDataCrud
 * @projectName im
 * @description: TODO
 * @date 2019-07-07 15:26
 * @ltd：思为
 */
@DataWay(1)
@Slf4j
public class MysqlDataCrud implements DataCrudStrategy {

   // private  RedisUtil redisUtil = (RedisUtil)SpringContextUtil.getBean("redisUtil");

  //  private  ChatRecordMapper chatRecordMapper = (ChatRecordMapper)SpringContextUtil.getBean("chatRecordMapper");
   // private  ChatRoomMapper chatRoomMapper= (ChatRoomMapper)SpringContextUtil.getBean("chatRoomMapper");
   // private  DelRoomMapper   delRoomMapper= (DelRoomMapper)SpringContextUtil.getBean("delRoomMapper");
    /*private  IMDelRoomService  delRoomService= (IMDelRoomService)SpringContextUtil.getBean("delRoomService");*/
    @Autowired
    private IMChatRecordMapper chatRecordMapper;
    private IMChatRoomMapper chatRoomMapper;
    private IMDelRoomMapper delRoomMapper;


    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public int insertMsgData(ChatBody chatBody) {
        String  id = UUID.randomUUID().toString();
       // String key=redisUtil.get(BasicConstants.IMROOMKEY+chatBody.getFrom()+chatBody.getTo()) ==null ?  BasicConstants.IMROOMKEY+chatBody.getTo()+chatBody.getFrom():BasicConstants.IMROOMKEY+chatBody.getFrom()+chatBody.getTo();
        Date date = new Date();
        //判断房间是否存在 如果存在则新增房间,如果没有则新增
      // if(redisUtil.get(chatBody.getFrom()+chatBody.getTo()) ==null || redisUtil.get(chatBody.getTo()+chatBody.getFrom())==null){
        //房间信息入库
      //  if(redisUtil.get(BasicConstants.IMROOMKEY+chatBody.getFrom()+chatBody.getTo()) ==null && redisUtil.get(BasicConstants.IMROOMKEY+chatBody.getTo()+chatBody.getFrom())==null){
            //从数据库中查询出room_id
            ChatUserListVO chatUserListVO=chatRoomMapper.getChatRoomInfo(chatBody.getFrom(),chatBody.getTo());
            if(chatUserListVO!=null){
                id=chatUserListVO.getRoomId();
            }else{
                //如果发送方或者接收方为空则直接返回错误码
                if(chatBody.getFrom()==null || chatBody.getTo()==null){
                    return 500;
                }
                IMChatRoom chatRoom =new IMChatRoom().setId(id).setCreateUserId(chatBody.getFrom()).setUserId(chatBody.getTo()).setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
                chatRoomMapper.insert(chatRoom);
               // redisUtil.set(BasicConstants.IMROOMKEY+chatBody.getFrom()+chatBody.getTo(),chatRoom);
                //插入房间时把聊天列表也需要更新
            }
   //    }else{
        //   Object roomInfo=redisUtil.get(key);
      //     if(roomInfo!=null && roomInfo!=""){
        //       JSONObject json1= JSONObject.parseObject(roomInfo.toString());
      //         id = json1.getString("id");
    //       }
    //   }
        //聊天信息入库
        IMChatRecord chatRecord =new IMChatRecord().setMsgId(UUID.randomUUID().toString()).setTargetId(chatBody.getFrom()).setFromId(chatBody.getTo())
         .setMsgType(chatBody.getMsgType()+"").setMsgContent(chatBody.getContent()).setMsgCreateTime(LocalDateTimeUtils.format(new Date(),LocalDateTimeUtils.DateFormatter.YYYYMMDD_HHMMSS_FORMATTER))
        .setIsRead(0).setIsToUser(0).setIsFromUser(0).setIsAutoSend(1).setCreatedAt(LocalDateTime.now()).setRoomId(id).setJsonContent(JSONObject.toJSONString(chatBody));
        int relsut=chatRecordMapper.insert(chatRecord);
        //离线消息
       // Object info=redisUtil.get(BasicConstants.IMUSERKEY+chatBody.getTo());
      //  chatRecord.setIsOnline(info==null ? "1":"0");


//        List<Object> list =redisUtil.lGet(id,0,-1);
//        //插入首位置 方便查询出来是倒序
//        list.add(0,chatRecord);
//        redisUtil.lSet(BasicConstants.IMCHATKEY+id,list);

        //删除 删除对话记录
        IMDelRoom delRoom =delRoomMapper.getDelRoomInfo(id);
        if(delRoom!=null){
            delRoomMapper.deleteDelRoomInfo(delRoom.getRoomId());
        }
        return relsut;
    }



}
