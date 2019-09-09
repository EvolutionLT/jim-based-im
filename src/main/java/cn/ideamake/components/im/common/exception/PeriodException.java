package cn.ideamake.components.im.common.exception;

import cn.ideamake.components.im.common.enums.RestEnum;

public class PeriodException extends AbstractException {
    protected PeriodException(RestEnum restEnum, Exception e) {
        super(restEnum, e);
    }

    public PeriodException(String msg) {
        super(msg);
    }

    public PeriodException(RestEnum restEnum) {
        super(restEnum);
    }

    public PeriodException(String errMsg, Exception e) {
        super(errMsg,e);
    }
}
