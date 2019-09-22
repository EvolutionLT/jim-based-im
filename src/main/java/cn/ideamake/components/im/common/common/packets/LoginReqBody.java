/**
 *
 */
package cn.ideamake.components.im.common.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月12日 下午3:13:22
 */
public class LoginReqBody {

    private static final long serialVersionUID = -10113316720288444L;

    private String userId;

    private String password;

    private String token;

    /**
     * 消息cmd命令码
     */
    protected Integer cmd;

    public LoginReqBody() {
    }

    public LoginReqBody(String token) {
        this.token = token;
        this.cmd = Command.COMMAND_LOGIN_REQ.getNumber();
    }

    public LoginReqBody(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.cmd = Command.COMMAND_LOGIN_REQ.getNumber();
    }

    public LoginReqBody(String userId, String password, String token) {
        this(userId, password);
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
