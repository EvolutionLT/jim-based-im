package cn.ideamake.components.im.pojo.constant;

/**
 * @program jio-based-im
 * @description: 终端用户类型
 * @author: apollo
 * @create: 2019/09/18 18:09
 */
public enum UserType {
    //身份类型,1=客服,0=访客, 2置业顾问
    VISITOR(0, "访客", "您联系的客户目前不在线，可给客户留言!"),
    CUSTOMER(1, "客服", "您的专属客服目前不在线，可给客服留言!"),
    ESTATE_AGENT(2, "置业顾问", "您的专属置业顾问目前不在线，可给置业顾问留言!")
    ;

    private Integer type;

    private String desc;

    private String message;


    UserType(Integer type, String desc, String message) {
        this.type = type;
        this.desc = desc;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static UserType getUserType(Integer type) {
        if(type == null) { return null;}
        return VISITOR.getType() == type ? VISITOR : CUSTOMER.getType() == type ? CUSTOMER : ESTATE_AGENT;
    }
}
