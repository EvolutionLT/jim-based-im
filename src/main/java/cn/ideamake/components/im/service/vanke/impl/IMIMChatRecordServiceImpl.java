package cn.ideamake.components.im.service.vanke.impl;


import cn.ideamake.common.response.IdeamakePage;
import cn.ideamake.common.response.Result;
import cn.ideamake.components.im.dto.mapper.IMChatRecordMapper;
import cn.ideamake.components.im.pojo.dto.ChatMsgListDTO;
import cn.ideamake.components.im.pojo.entity.IMChatRecord;
import cn.ideamake.components.im.pojo.vo.ChatMsgListVO;
import cn.ideamake.components.im.service.vanke.IMChatRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class IMIMChatRecordServiceImpl extends ServiceImpl<IMChatRecordMapper, IMChatRecord> implements IMChatRecordService {

    @Autowired
    private IMChatRecordMapper chatRecordMapper;

    @Override
    public int insertChatRecord(IMChatRecord chatRecord) {
        return chatRecordMapper.insert(chatRecord);
    }

    @Override
    public Result getMsgList(ChatMsgListDTO chatMsgListQuery) {
        Result apiResponse = Result.ok();
        List<Object> list= new ArrayList<>();
  /*      if(chatMsgListQuery.getPage()==1){
            list=redisUtil.lGet(BasicConstants.IMCHATKEY+chatMsgListQuery.getRoomId(),0,100);
        }
*/
       // if(list==null || list.size()==0){
            IdeamakePage ideamakePage = new IdeamakePage(chatMsgListQuery.getPage(),chatMsgListQuery.getLimit());
            List<ChatMsgListVO> listVOList =chatRecordMapper.getMsgList(ideamakePage,chatMsgListQuery);
            ideamakePage.setList(listVOList);
            apiResponse.setData(ideamakePage);
            //把当前查询用户的房间消息置为已读 只会更新当前用户
            if(chatMsgListQuery.getId()!=null && chatMsgListQuery.getId()!=""){
                 updateChatRecordStatus(chatMsgListQuery);
            }
      //  }else{
      //      apiResponse.setData(list);
     //   }
        return apiResponse;
    }

    @Override
    public Result updateChatRecordStatus(ChatMsgListDTO chatMsgListQuery) {
        Result apiResponse = Result.ok();
         chatRecordMapper.updateChatRecordStatus(chatMsgListQuery);
        return apiResponse;
    }

    @Override
    public Result getUserMsgNotRead(ChatMsgListDTO chatMsgListQuery) {
        Result apiResponse = Result.ok();
        if(chatMsgListQuery.getId()!=null) {
            int notRead = chatRecordMapper.getUserMsgNotRead(chatMsgListQuery.getRoomId(), "0", chatMsgListQuery.getId());
            apiResponse.setData(notRead);
        }
        return apiResponse;
    }
}
