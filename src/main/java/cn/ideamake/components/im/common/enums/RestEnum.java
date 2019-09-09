package cn.ideamake.components.im.common.enums;

import lombok.Getter;

@Getter
public enum RestEnum {

    /** 成功 */
    SUCCESS(0, " 操作成功"),

    /** 内部错误 */
    ERROR(500, "内部错误"),


    /** 业务异常 */
    FAIL(500101, "业务异常"),

    /** 参数错误 */
    PARAMETER_ERROR(500102, "参数错误"),




    /** token过期 */
    TOKEN_EXPIRED(500201, "token expired"),
    /** 未登录 */
    NEED_LOGIN(500202, "no login"),
    TOKEN_ERROR(500203, "token error"),
    REFRESH_TOKEN_EXPIRED(500204, "refresh token expired"),
    USER_NOT_FOUND(500205, "user not found"),
    NOT_BINDING(500206, "visitor not binding for the project"),

    /** 没有访问权限 */
    NO_ACCESS_PRIVILEGES(500301, "没有该资源的访问权限"),
    /** 需要登录 */
    NO_LOGIN(500302, "need login"),
    ;

    private Integer code;
    private String msg;

    RestEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
