package cn.ideamake.components.im.web.vanke;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.common.common.ImConst;
import cn.ideamake.components.im.common.common.cache.redis.RedisCache;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import cn.ideamake.components.im.common.common.cache.redis.RedissonTemplate;
import cn.ideamake.components.im.common.common.packets.User;
import cn.ideamake.components.im.pojo.constant.TermianlType;
import cn.ideamake.components.im.pojo.constant.VankeRedisKey;
import cn.ideamake.components.im.pojo.dto.ChatInfoDTO;
import cn.ideamake.components.im.pojo.dto.VankeLoginDTO;
import cn.ideamake.components.im.pojo.vo.ChatInfoVO;
import cn.ideamake.components.im.service.vanke.ValidAuthorService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

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
        log.info("VankeController-login(), input: {}", JSON.toJSONString(info));
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
        log.info("VankeController-getReceiverInfo(), input: {}", JSON.toJSONString(dto));
        String receiverId = dto.getReceiverId();
        @NotNull Integer type = dto.getType();
        //置业顾问或者客服发送消息时，接收人不能为空
        if (StringUtils.isBlank(receiverId) && type != TermianlType.VISITOR.getType().intValue()) {
            throw new IllegalArgumentException("receiverId is null! input: {}" + JSON.toJSONString(dto));
        }
        try {
            User info = validAuthorService.getReceiverInfo(dto);
            log.info("VankeController-getReceiverInfo(), result: {}", JSON.toJSONString(info));
            return info == null ? Rest.error("当前客服繁忙，建议您电话咨询!") : Rest.okObj(info);
        } catch (Exception e) {
            log.error("VankeController-getReceiverInfo(), is error, error: ", e);
            return Rest.error("当前客服繁忙，建议您电话咨询!");
        }
    }

    @PostMapping("/chatInfo")
    public Rest<ChatInfoVO> getChatInfo(@RequestBody ChatInfoDTO dto) {
        @NotBlank String visitorId = dto.getVisitorId();
        @NotBlank String cusId = dto.getCusId();
        // 是否是新成员 0=好友，1=新人
        @NotNull Integer isNewMember = dto.getIsNewMember();
        //操作 1=增加 2删除
        @NotNull Integer op = dto.getOp();
        //是否是当前聊天成员 0=不是 1=是
        @NotNull Integer isConcurrent = dto.getIsConcurrent();
        Objects.requireNonNull(visitorId, "VankeController-getChatInfo(), visitorId is null!");
        Objects.requireNonNull(cusId, "VankeController-getChatInfo(), cusId is null!");
        ChatInfoVO vo = new ChatInfoVO();
        String allCountKey = String.format(VankeRedisKey.VANKE_CHAT_MEMBER_NUM_KEY, cusId);
        String unReadNumKey = String.format(VankeRedisKey.VANKE_CHAT_UNREAD_NUM_KEY, cusId, visitorId);
        String pendingReplyNumKey = String.format(VankeRedisKey.VANKE_CHAT_PENDING_REPLY_NUM_KEY, cusId);
        String lastedContactNumKey = String.format(VankeRedisKey.VANKE_CHAT_LASTED_CONTACT_SNUM_KEY, cusId);
        RedisCache cache = RedisCacheManager.getCache(ImConst.USER);
        RMapCache<String, Long> mapCache = RedissonTemplate.me().getRedissonClient().getMapCache(pendingReplyNumKey);

        // 监听到消息
        if (op == 1) {
            //刚分配的新访客
//            if (isNewMember == 1) {
//                vo.setLastedContactsNum(cache.incr(lastedContactNumKey, 1));
//            }
            //不是当前聊天访客
            if (isConcurrent == 0) {
                vo.setUnReadNum(cache.incr(unReadNumKey, 1));
                if (mapCache.containsKey(visitorId)) {
                    vo.setPendingReplyNum(Long.valueOf(mapCache.size()));
                } else {
                    mapCache.put(visitorId, 1L);
                    vo.setPendingReplyNum(Long.valueOf(mapCache.size()));
                }
            }
        }

        //点击聊天框，清空未读消息
        if (op == 2 && isConcurrent == 1) {
            cache.remove(unReadNumKey);
            vo.setUnReadNum(0L);
            mapCache.remove(visitorId);
            vo.setPendingReplyNum(Long.valueOf(mapCache.size()));
        }

        vo.setAllContactsNum(cache.get(allCountKey, Long.class));

        vo.setLastedContactsNum(cache.get(lastedContactNumKey, Long.class));

        if(Objects.isNull(vo.getPendingReplyNum())) {
            vo.setPendingReplyNum(Long.valueOf(mapCache.size()));
        }

        if(Objects.isNull(vo.getUnReadNum())) {
            vo.setUnReadNum(cache.get(unReadNumKey, Long.class));
        }
        vo.setVisitorId(visitorId);
        vo.setCusId(cusId);
        return Rest.okObj(vo);
    }

}
