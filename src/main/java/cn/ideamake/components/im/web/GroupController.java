package cn.ideamake.components.im.web;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.pojo.dto.GroupInsertDTO;
import cn.ideamake.components.im.pojo.dto.UserGroupDTO;
import cn.ideamake.components.im.service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Walt
 * @date 2019-09-14 20:20
 */
@RestController
public class GroupController {

//    @Autowired
//    private PeriodService periodService;

//    /**
//     * 添加群组
//     * @param groupInsertDTO
//     * @return
//     */
//    @PostMapping("/group/save")
//    public Rest createGroup(GroupInsertDTO groupInsertDTO){
//        return Rest.okObj(periodService.addGroup(groupInsertDTO));
//    }
//
//    /**
//     * 删除群组
//     * @param userId
//     * @return
//     */
//    @DeleteMapping("/group/{token}/{userId}")
//    public Rest deleteGroup(@PathVariable String userId, @PathVariable String token){
//        periodService.deleteGroup(userId,token);
//        return Rest.ok();
//    }
//
//    /**
//     * 添加用户到群聊组
//     * @return
//     */
//    @PostMapping("/group/user/save")
//    public Rest addUserToGroup(UserGroupDTO userGroupDTO){
//        periodService.addUserToGroup(userGroupDTO);
//        return Rest.ok();
//    }
//
//    /**
//     * 从用户组中删除用户
//     * @return
//     */
//    @PostMapping("/group/user/delete")
//    public Rest removeUserFromGroup(UserGroupDTO userGroupDTO){
//        periodService.removeUserFromGroup(userGroupDTO);
//        return Rest.ok();
//    }



}
