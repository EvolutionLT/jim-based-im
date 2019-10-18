package cn.ideamake.components.im.web.vanke;



import cn.ideamake.common.response.Result;
import cn.ideamake.components.im.dto.mapper.IMChatRoomMapper;
import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMChatRoom;
import cn.ideamake.components.im.pojo.vo.ChatUserListVO;
import cn.ideamake.components.im.service.vanke.IMChatRoomService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evolution
 * @since 2019-07-07
 */
@RestController
@RequestMapping("/chatRoom")
public class IMChatRoomController {

    @Autowired
    private IMChatRoomService chatRoomService;
    @Autowired
    private IMChatRoomMapper chatRoomMapper;

    /**
     * 查询对话列表
     * @param chatUserListQuery
     * @param request
     * @return
     */
    @RequestMapping("/getUserList")
    public Result getAllUserList(ChatUserListDTO chatUserListQuery, HttpServletRequest request) {
        return chatRoomService.getAllChatUserList(chatUserListQuery);
    }

    @RequestMapping("/add")
    public Result add(ChatUserListDTO chatUserListQuery, HttpServletRequest request){
        if(StringUtils.isBlank(chatUserListQuery.getFrom()) || StringUtils.isBlank(chatUserListQuery.getTo())){
            return  Result.error("ID不能为空！");
        }
        ChatUserListVO chatUserListVO=chatRoomMapper.getChatRoomInfo(chatUserListQuery.getFrom(),chatUserListQuery.getTo());
        if(chatUserListVO!=null){
            return Result.ok(chatUserListVO.getRoomId());
        }
        IMChatRoom chatRoom = new IMChatRoom();
        String id=UUID.randomUUID().toString();
        chatRoom.setId(id);
        chatRoom.setCreateUserId(chatUserListQuery.getFrom());
        chatRoom.setUserId(chatUserListQuery.getTo());
        chatRoom.setCreatedAt(LocalDateTime.now());
        chatRoom.setUpdatedAt(LocalDateTime.now());
        return  chatRoomService.insertChatRoom(chatRoom);
    }

    @PostMapping("/deleteRoom")
    public Result delete(ChatUserListDTO chatUserListQuery){
        return  chatRoomService.deleteRoom(chatUserListQuery);
    }




}

