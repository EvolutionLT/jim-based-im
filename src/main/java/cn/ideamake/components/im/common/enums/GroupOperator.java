package cn.ideamake.components.im.common.enums;

import lombok.Getter;

/**
 * @author Walt
 * @date 2019-09-14 15:30
 */
@Getter
public enum GroupOperator {

    DELETE(1,"删除组"),
    ADD(2,"添加组"),
    GET(3,"获取组"),
    UPDATE(4,"更新组"),


    ;

    private Integer code;
    private String msg;

    GroupOperator(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
