package cn.ideamake.components.im.web.vanke;


import cn.ideamake.common.response.Result;
import cn.ideamake.components.im.pojo.dto.ChatMsgListDTO;
import cn.ideamake.components.im.service.vanke.IMChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evolution
 * @since 2019-07-07
 */
@RestController
@RequestMapping("/chatRecord")
public class IMChatRecordController {

    @Autowired
    private IMChatRecordService chatRecordService;


    /**
     * 查询用户聊天记录
     * @param chatMsgListQuery
     * @param request
     * @return
     */
    @RequestMapping("/getUserChat")
    public Result getUserChatRecord(ChatMsgListDTO chatMsgListQuery, HttpServletRequest request){
      return chatRecordService.getMsgList(chatMsgListQuery);
    }



    /**
     * 查询用户未读消息总数
     * @param chatMsgListQuery
     * @param request
     * @return
     */
    @RequestMapping("/getNotRead")
    public Result getAllMsgNotRead(ChatMsgListDTO chatMsgListQuery, HttpServletRequest request){
    return chatRecordService.getUserMsgNotRead(chatMsgListQuery);
    }

    /**
     * 修改消息状态
     * @param chatMsgListQuery
     * @param request
     * @return
     */
    @RequestMapping("/updateMsg")
    public Result updateMsgStatus(ChatMsgListDTO chatMsgListQuery, HttpServletRequest request){
        return chatRecordService.updateChatRecordStatus(chatMsgListQuery);
    }



}

