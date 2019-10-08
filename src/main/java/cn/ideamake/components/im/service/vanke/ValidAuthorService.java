package cn.ideamake.components.im.service.vanke;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.common.server.command.handler.processor.login.LoginCmdProcessor;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;

public interface ValidAuthorService extends LoginCmdProcessor {
    /**
     * @description: 校验权限
     * @param: [dto]
     * @return: void
     * @author: apollo
     * @date: 2019-09-17
     */
    void initUserInfo(VankeLoginDTO dto);

    /**
    * @description: 获取聊天对象信息并且建立tcp通道，比如当访客点击咨询时自动分配置业顾问或者客服
    * @param: [dto]
    * @return: void
    * @author: apollo
    * @date: 2019-09-19
    */
    User getReceiverInfo(VankeLoginDTO dto);

}
