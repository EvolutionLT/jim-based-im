package cn.ideamake.components.im.common.exception;


import cn.ideamake.components.im.common.enums.RestEnum;
import lombok.Getter;

@Getter
public abstract class AbstractException extends RuntimeException {

    /**
     * 响应码
     */
    protected final int code;
    /**
     * 响应消息
     */
    protected final String msg;

    protected AbstractException(RestEnum restEnum, Exception e) {
        super(restEnum.getMsg(), e);
        this.code = restEnum.getCode();
        this.msg = restEnum.getMsg();
    }

    protected AbstractException(RestEnum restEnum) {
        super(restEnum.getMsg());
        this.code = restEnum.getCode();
        this.msg = restEnum.getMsg();
    }

    protected AbstractException(String errorMsg) {
        super(errorMsg);
        this.code = 10086;
        this.msg = errorMsg;
    }

    protected AbstractException(String errorMsg, Exception e) {
        super(errorMsg, e);
        this.code = 10086;
        this.msg = errorMsg;
    }

    public AbstractException(Exception e) {
        this(RestEnum.ERROR, e);
    }
}
