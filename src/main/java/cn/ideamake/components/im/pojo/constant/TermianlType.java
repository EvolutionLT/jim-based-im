package cn.ideamake.components.im.pojo.constant;

/**
 * @program jio-based-im
 * @description: 终端用户类型
 * @author: apollo
 * @create: 2019/09/18 18:09
 */
public enum  TermianlType {
    //身份类型,1=客服,0=访客, 2置业顾问
    VISITOR(0, "访客"),
    CUSTOMER(1, "客服"),
    ESTATE_AGENT(2, "置业顾问")
    ;

    private Integer type;

    private String desc;

    TermianlType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
