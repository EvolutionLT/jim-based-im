package cn.ideamake.components.im.web;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.pojo.dto.LoginDTO;
import cn.ideamake.components.im.pojo.dto.UserDTO;
import cn.ideamake.components.im.service.PeriodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 时代相关接口
 */
@RestController
@Slf4j
public class PeriodController {

    /**
     * 用户通过用户名和密码换取token
     * @return
     */
    @Autowired
    public PeriodService periodService;

    @PostMapping("/period/token")
    public Rest getToken(UserDTO user){
        log.info(periodService.getUserInfoById("asd").toString());
        return Rest.ok();
    }

    @PostMapping("/period/login")
    public Rest userLogin(@RequestBody LoginDTO loginDTO){
        String token = periodService.loginInfoToToken(loginDTO);
        return Rest.okObj(token);
    }
    //模拟应用服务器接口
    /**
     *
     * @param loginDTO
     * @return
     */
    @PostMapping("/app/period/im-login")
    public String loginCheck(@RequestBody LoginDTO loginDTO){
        log.info("app server:"+loginDTO.toString());
        return "success";
    }
}
