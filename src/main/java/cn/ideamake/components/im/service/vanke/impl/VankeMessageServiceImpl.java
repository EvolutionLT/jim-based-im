package cn.ideamake.components.im.service.vanke.impl;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import cn.ideamake.components.im.common.common.packets.Command;
import cn.ideamake.components.im.common.common.utils.Md5;
import cn.ideamake.components.im.dto.mapper.*;
import cn.ideamake.components.im.pojo.constant.TermianlType;
import cn.ideamake.components.im.pojo.constant.VankeChatStaus;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.pojo.entity.*;
import cn.ideamake.components.im.service.vanke.VankeMessageService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Slf4j
public class VankeMessageServiceImpl implements VankeMessageService {
    @Resource
    private CusChatMemberMapper cusChatMemberMapper;

    @Resource
    private CusChatRoomMapper cusChatRoomMapper;

    @Resource
    private CusChatMessageMapper cusChatMessageMapper;

    @Resource
    private CusChatRoomRelateMapper cusChatRoomRelateMapper;

    @Resource
    private CusVisitorMapper cusVisitorMapper;

    @Resource
    private VisitorMapper visitorMapper;

    @Resource
    private CusInfoMapper cusInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeMessage(ChatBody chatBody, int cmd) {
        //发起聊天
        if (Command.COMMAND_CHAT_REQ.getNumber() == cmd && chatBody != null) {
            try {
                addChatRecord(chatBody);
            } catch (Exception e) {
                throw e;
            }
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChatMember(String userId, int op) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        CusChatMember member = cusChatMemberMapper.selectByUserId(userId);
        //上线操作
        if (op == VankeChatStaus.ON_LINE.getStatus()) {
            Integer status = member.getStatus();
            if (VankeChatStaus.ON_LINE.getStatus().intValue() == status) {
                return;
            }
            //修改状态为上线
            cusChatMemberMapper.updateStatus(member.getId(), VankeChatStaus.ON_LINE.getStatus(), 0);
            return;
        }
        //下线操作
        if (op == VankeChatStaus.OFF_LINE.getStatus()) {
            //修改成员为下线
            cusChatMemberMapper.updateStatus(member.getId(), VankeChatStaus.OFF_LINE.getStatus(), 1);
            //修改聊天房间相关数据为下线状态
            cusChatRoomRelateMapper.updateStatus(member.getId(), VankeChatStaus.OFF_LINE.getStatus());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initChatInfo(VankeLoginDTO dto) {
        log.info("VankeMessageService-initChatInfo(), init start, input: {}", JSON.toJSONString(dto));
        @NotBlank String senderId = dto.getSenderId();
        @NotBlank String receiverId = dto.getReceiverId();
        @NotNull Integer type = dto.getType();
        Date date = new Date();
        //初始化访客信息
        boolean isSuccess = initMember(dto) && type == TermianlType.VISITOR.getType().intValue();
        log.info("VankeMessageService-initChatInfo(), init start, isSuccess: {}", isSuccess);
        if (isSuccess) {
            //初始化聊天房间
            String uniqueCode = Md5.getMD5(senderId + receiverId);
            CusChatRoom cusChatRoom = new CusChatRoom();
            cusChatRoom.setStatus(1);
            cusChatRoom.setUniqueCode(uniqueCode);
            cusChatRoom.setCreateAt(date);
            cusChatRoomMapper.insert(cusChatRoom);
            //初始化聊天关系表
            Integer roomId = cusChatRoom.getId();
            insertRelate(roomId, senderId, type);
            insertRelate(roomId, receiverId, type);
            //初始化wk_cus_visitor表
            CusVisitor cusVisitor = new CusVisitor();
            cusVisitor.setCusId(receiverId);
            cusVisitor.setVisitorId(senderId);
            cusVisitor.setStatus(1);
            cusVisitor.setCreateBy("0");
            cusVisitor.setUpdateBy("0");
            cusVisitor.setCreateAt(date);
            cusVisitorMapper.insert(cusVisitor);
            //把客服状态修改为繁忙 0=空闲， 1=繁忙
            cusChatMemberMapper.updateIsBusy(receiverId, 1);
            log.info("VankeMessageService-initChatInfo(), init end, input: {}", JSON.toJSONString(dto));
        }
    }

    @Override
    public boolean initMember(VankeLoginDTO dto) {
        CusChatMember cusChatMember = cusChatMemberMapper.selectByUserId(dto.getSenderId());
        if (Objects.isNull(cusChatMember)) {
            @NotNull Integer type = dto.getType();
            CusChatMember entity = new CusChatMember();
            entity.setUserId(dto.getSenderId());
            entity.setHeadImgUrl(dto.getAvatar());
            entity.setNickName(dto.getNick());
            //发送人身份类型,1=客服,0=访客, 2置业顾问
            String phone = "";
            if (0 == type) {
                phone = Optional.ofNullable(visitorMapper.selectByOpenId(dto.getSenderId())).map(Visitor::getPhone).orElse("");
            } else if (1 == type) {
                phone = Optional.ofNullable(cusInfoMapper.selectById(dto.getSenderId())).map(CusInfo::getPhone).orElseThrow(() -> new IllegalArgumentException("visitorId is error, visitorId: " + dto.getSenderId()));
            }
            entity.setPhone(phone);
            entity.setType(dto.getType());
            entity.setToken(dto.getToken());
            entity.setStatus(VankeChatStaus.ON_LINE.getStatus());
            entity.setCreateAt(new Date());
            return 1 == cusChatMemberMapper.insert(entity);
        }
        return false;
    }

    private void addChatRecord(ChatBody chatBody) {
        String sender = chatBody.getFrom();
        String receiver = chatBody.getTo();
        String uniqueCode = Md5.getMD5(sender + receiver);
        Integer roomId = Optional.ofNullable(cusChatRoomMapper.selectByUniqueCode(uniqueCode)).map(CusChatRoom::getId).orElse(-1);
        insertMessage(chatBody, sender, receiver, roomId);
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
        roomRelate.setUserId(userId);
        roomRelate.setType(type);
        roomRelate.setStatus(VankeChatStaus.ON_LINE.getStatus());
        roomRelate.setCreateAt(new Date());
        Integer memberId = Optional.ofNullable(cusChatMemberMapper.selectByUserId(userId)).map(CusChatMember::getId).orElseThrow(
                () -> new IllegalArgumentException("userId 没有初始化到member表中， userId：" + userId)
        );
        roomRelate.setChatMemberId(memberId);
        cusChatRoomRelateMapper.insert(roomRelate);

    }
}
