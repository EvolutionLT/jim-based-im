package cn.ideamake.components.im.pojo.constant;

public enum VankeChatStaus {
    //状态 0=无效, 1=生效中，2=已关闭(下线)
    INVALID(0, "无效的"),
    ON_LINE(1, "正常(在线或者有效)"),
    OFF_LINE(2, "已关闭(下线)")
    ;

    private Integer status;

    private String desc;

    VankeChatStaus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
