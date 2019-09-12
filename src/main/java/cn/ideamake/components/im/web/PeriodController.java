package cn.ideamake.components.im.web;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.pojo.dto.GroupInsertDTO;
import cn.ideamake.components.im.pojo.dto.LoginDTO;
import cn.ideamake.components.im.pojo.dto.UserDTO;
import cn.ideamake.components.im.pojo.vo.UserAuthVO;
import cn.ideamake.components.im.service.PeriodService;
import cn.ideamake.components.im.service.impl.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 添加群组
     * @param groupInsertDTO
     * @return
     */
    @PostMapping("/group/save")
    public Rest createGroup(GroupInsertDTO groupInsertDTO){
       return Rest.okObj(periodService.addGroup(groupInsertDTO));
    }

    /**
     * 删除群组
     * @param userId
     * @return
     */
    @DeleteMapping("/group/{token}/{userId}")
    public Rest deleteGroup(@PathVariable String userId,@PathVariable String token){
        periodService.deleteGroup(userId,token);
        return Rest.ok();
    }

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
