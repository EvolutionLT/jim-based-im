package cn.ideamake.components.im.common.exception;

import cn.ideamake.components.im.common.enums.RestEnum;

public class IMException extends AbstractException {
    protected IMException(RestEnum restEnum, Exception e) {
        super(restEnum, e);
    }

    public IMException(String msg) {
        super(msg);
    }

    public IMException(RestEnum restEnum) {
        super(restEnum);
    }

    public IMException(String errMsg, Exception e) {
        super(errMsg,e);
    }
}
