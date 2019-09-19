package cn.ideamake.components.im.pojo.constant;

/**
 * @program jio-based-im
 * @description: 终端用户类型
 * @author: apollo
 * @create: 2019/09/18 18:09
 */
public enum  TermianlType {
    //身份类型,0=客服,1=访客, 2置业顾问
    CUSTOMER(0, "客服"),
    VISITOR(2, "访客"),
    ESTATE_AGENT(3, "置业顾问")
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
