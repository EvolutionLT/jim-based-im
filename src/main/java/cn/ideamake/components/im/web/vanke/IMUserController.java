package cn.ideamake.components.im.web.vanke;


import cn.ideamake.common.response.Result;
import cn.ideamake.components.im.pojo.dto.ChatUserListDTO;
import cn.ideamake.components.im.pojo.entity.IMUsers;
import cn.ideamake.components.im.service.vanke.IMUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evolution
 * @since 2019-07-02
 */
@RestController
@RequestMapping("/user")
public class IMUserController {

    @Autowired
    private IMUserService userService ;





    @RequestMapping("hello")
    public IMUsers getUser(){
        //redisTemplate.opsForValue().set("111","11111111");
        IMUsers imUser=userService.getUserInfo("13434782994");
      //  System.out.println("***"+loginService.getUserInfo("13434782994"));
        return imUser;

    }

    /**
     * 查询用户接待人数 以及回复速度 等相关信息
     * @return
     */
    @RequestMapping("info")
    public Result getUserInfo(HttpServletRequest request, ChatUserListDTO chatUserListQuery){
        Result apiResponse=Result.ok();
        IMUsers user= userService.getUserInfo(chatUserListQuery.getUuid());
        apiResponse.setData(user);
        return apiResponse;

    }

    /**
     * 查询当前在线的所有用户
     * @return
     */
    @RequestMapping("all")
    public Result getAllUser(HttpServletRequest request, ChatUserListDTO chatUserListQuery){
        Result apiResponse=Result.ok();
        List<IMUsers> list=userService.getAllUser(chatUserListQuery);
        apiResponse.setData(list);
        return apiResponse;

    }

}


