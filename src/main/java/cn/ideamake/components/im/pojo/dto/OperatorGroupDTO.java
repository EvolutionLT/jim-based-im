package cn.ideamake.components.im.pojo.dto;

import cn.ideamake.components.im.common.common.packets.Group;
import cn.ideamake.components.im.common.enums.GroupOperator;
import lombok.Data;

/**
 * @author Walt
 * @date 2019-09-14 15:28
 */
@Data
public class OperatorGroupDTO {
    private String token;
    private GroupOperator operatorId;
    private Group group;
    private String userId;
    private String groupId;
}
