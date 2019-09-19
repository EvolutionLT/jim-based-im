package cn.ideamake.components.im.web.vanke;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.pojo.constant.TermianlType;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.service.vanke.ValidAuthorService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @program jio-based-im
 * @description: 万科登录controller
 * @author: apollo
 * @create: 2019/09/17 15:31
 */
@RestController
@RequestMapping("/vanke")
@Slf4j
public class VankeController {
    @Resource
    private ValidAuthorService validAuthorService;

    @PostMapping(value = "/initUser", produces = "application/json")
    public Rest login(@Valid @RequestBody VankeLoginDTO info) {
        log.info("VankeController-login(), input: {}" , JSON.toJSONString(info));
        try {
            validAuthorService.initUserInfo(info);
        } catch (Exception e) {
            log.error("VankeController-initImUserInfo(), is error, error: ", e);
            return Rest.error("登录IM系统失败!");
        }
        return Rest.ok();
    }


    @PostMapping(value = "/getReceiver", produces = "application/json")
    public Rest<User> getReceiverInfo(@Valid @RequestBody VankeLoginDTO dto) {
        log.info("VankeController-getReceiverInfo(), input: {}" , JSON.toJSONString(dto));
        String receiverId = dto.getReceiverId();
        @NotNull Integer type = dto.getType();
        //置业顾问或者客服发送消息时，接收人不能为空
        if(StringUtils.isBlank(receiverId) && type != TermianlType.VISITOR.getType().intValue()) {
            throw new IllegalArgumentException("receiverId is null! input: {}" + JSON.toJSONString(dto));
        }
        try {
            User info = validAuthorService.getReceiverInfo(dto);
            log.info("VankeController-getReceiverInfo(), result: {}" , JSON.toJSONString(info));
            return Rest.okObj(info);
        } catch (Exception e) {
            log.error("VankeController-getReceiverInfo(), is error, error: ", e);
            return Rest.error("当前客服繁忙，建议您电话咨询!");
        }
    }

}
