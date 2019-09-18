package cn.ideamake.components.im.web;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.pojo.dto.LoginDTO;
import cn.ideamake.components.im.pojo.vo.UserAuthVO;
import cn.ideamake.components.im.service.PeriodService;
import cn.ideamake.components.im.service.impl.UserInfo;
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

    /**
     * 用户登录im
     * @param loginDTO
     * @return
     */
    @PostMapping("/period/login")
    public Rest userLogin(LoginDTO loginDTO){
        UserAuthVO userAuthVO = periodService.loginInfoToToken(loginDTO);
        return Rest.okObj(userAuthVO);
    }


    //模拟应用服务器接口
    /**
     *
     * @param loginDTO
     * @return
     */
    @PostMapping("/app/period/im-login")
    public Rest loginCheck(@RequestBody LoginDTO loginDTO){
        log.info("app server:"+loginDTO.toString());
        UserInfo user = new UserInfo(loginDTO.getUserId(),loginDTO.getPassword());
        return Rest.okObj(user);
    }


}
