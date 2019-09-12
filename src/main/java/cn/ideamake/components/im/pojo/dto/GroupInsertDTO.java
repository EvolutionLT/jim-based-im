package cn.ideamake.components.im.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Walt
 * @date 2019-09-11 12:02
 */
@Data
public class GroupInsertDTO {
    @NotNull(message = "请携带token操作")
    private String token;
    private String name;//群组名称;
    private String avatar;//群组头像;
}
