package cn.ideamake.components.im.common;

public class Rest<T> {

    private int code;
    private String msg;
    private T data;

    public Rest(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Rest(){

    }

    public Rest(T data) {
        this.data = data;
    }

    public Rest<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Rest<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Rest<T> setData(T data) {
        this.data = data;
        return this;
    }
}
