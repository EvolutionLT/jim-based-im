package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.common.common.utils.Md5;
import cn.ideamake.components.im.common.constants.Constants;
import cn.ideamake.components.im.dto.mapper.CusChatMemberMapper;
import cn.ideamake.components.im.dto.mapper.CusChatMessageMapper;
import cn.ideamake.components.im.dto.mapper.CusChatRoomMapper;
import cn.ideamake.components.im.dto.mapper.CusChatRoomRelateMapper;
import cn.ideamake.components.im.pojo.constant.VankeChatStaus;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.pojo.entity.CusChatMember;
import cn.ideamake.components.im.pojo.entity.CusChatMessage;
import cn.ideamake.components.im.pojo.entity.CusChatRoom;
import cn.ideamake.components.im.pojo.entity.CusChatRoomRelate;
import cn.ideamake.components.im.service.vanke.VankeMessageService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @program jio-based-im
 * @description: 万科-聊天消息service
 * @author: apollo
 * @create: 2019/09/17 20:48
 */
@Service
public class VankeMessageServiceImpl implements VankeMessageService {
    @Resource
    private CusChatMemberMapper cusChatMemberMapper;

    @Resource
    private CusChatRoomMapper cusChatRoomMapper;

    @Resource
    private CusChatMessageMapper cusChatMessageMapper;

    @Resource
    private CusChatRoomRelateMapper cusChatRoomRelateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeMessage(ChatBody chatBody, int cmd) {
        //成功聊天中
        if(Command.COMMAND_CHAT_RESP.getNumber() == cmd && chatBody != null) {
            addChatRecord(chatBody);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChatMember(String userId, int op) {
        if(StringUtils.isBlank(userId)) {
            return ;
        }
        CusChatMember member = cusChatMemberMapper.selectByUserId(userId);
        //上线操作
        if(op == VankeChatStaus.ON_LINE.getStatus()) {
            if(Objects.isNull(member)) {
                //不存在，初始化
                VankeLoginDTO dto = RedisCacheManager.getCache(ImConst.USER).get(userId + ":" + Constants.USER.VANKE_USER_PREFIX, VankeLoginDTO.class);
                CusChatMember entity = new CusChatMember();
                entity.setUserId(userId);
                entity.setType(NumberUtils.toInt(dto.getTerminal()));
                entity.setToken(dto.getToken());
                entity.setStatus(VankeChatStaus.ON_LINE.getStatus());
                entity.setCreateAt(new Date());
                cusChatMemberMapper.insert(entity);
                return ;
            }
            Integer status = member.getStatus();
            if(VankeChatStaus.ON_LINE.getStatus().intValue() == status){
                return ;
            }
            //修改状态为上线
            cusChatMemberMapper.updateStatus(member.getId(), VankeChatStaus.ON_LINE.getStatus());
            return;
        }
        //下线操作
        if(op == VankeChatStaus.OFF_LINE.getStatus()) {
            //修改成员为下线
            cusChatMemberMapper.updateStatus(member.getId(), VankeChatStaus.OFF_LINE.getStatus());
            //修改聊天房间相关数据为下线状态
            cusChatRoomRelateMapper.updateStatus(member.getId(),  VankeChatStaus.OFF_LINE.getStatus());
        }


    }

    private void addChatRecord(ChatBody chatBody) {
        String sender = chatBody.getFrom();
        String receiver = chatBody.getTo();
        String uniqueCode = Md5.getMD5(sender + receiver);
        Integer roomId = Optional.ofNullable(cusChatRoomMapper.selectByUniqueCode(uniqueCode)).map(CusChatRoom::getId).orElse(null);
        if(Objects.isNull(roomId)) {
            //创建聊天房间
            CusChatRoom cusChatRoom = new CusChatRoom();
            cusChatRoom.setStatus(1);
            cusChatRoom.setUniqueCode(uniqueCode);
            cusChatRoom.setCreateAt(new Date());
            cusChatRoomMapper.insert(cusChatRoom);
            roomId = cusChatRoom.getId();
            insertRelate(roomId, chatBody.getFrom(), RedisCacheManager.getCache(ImConst.USER).get(sender + ":" + Constants.USER.VANKE_USER_PREFIX + ":" + Constants.USER.INFO, VankeLoginDTO.class).getType());
            insertRelate(roomId, chatBody.getTo(), RedisCacheManager.getCache(ImConst.USER).get(receiver + ":" + Constants.USER.VANKE_USER_PREFIX + ":" + Constants.USER.INFO, VankeLoginDTO.class).getType());
        }
        insertMessage(chatBody, sender, receiver, roomId);

        //缓存房间信息

    }

    private void insertMessage(ChatBody chatBody, String sender, String receiver, Integer roomId) {
        CusChatMessage message = new CusChatMessage();
        message.setChatType(chatBody.getChatType());
        message.setReceiverId(receiver);
        message.setChatRoomId(roomId);
        message.setMessageId(chatBody.getId());
        message.setMessageType(chatBody.getMsgType());
        message.setMessageContent(chatBody.getContent());
        message.setSenderId(sender);
        message.setStatus(VankeChatStaus.ON_LINE.getStatus());
        message.setSendTime(chatBody.getCreateTime());
        message.setCreateAt(new Date());
        cusChatMessageMapper.insert(message);
    }

    private void insertRelate(Integer roomId, String userId, Integer type) {
        CusChatRoomRelate roomRelate = new CusChatRoomRelate();
        roomRelate.setChatRoomId(roomId);
        roomRelate.setChatMemberId(userId);
        roomRelate.setType(type);
        roomRelate.setStatus(VankeChatStaus.ON_LINE.getStatus());
        roomRelate.setCreateAt(new Date());
        cusChatRoomRelateMapper.insert(roomRelate);

    }
}
