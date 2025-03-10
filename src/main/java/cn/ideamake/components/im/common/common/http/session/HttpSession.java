package cn.ideamake.components.im.common.common.http.session;

import cn.ideamake.components.im.common.common.ImSessionContext;
import cn.ideamake.components.im.common.common.http.HttpConfig;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wchao
 * 2017年8月5日 上午10:16:26
 */
public class HttpSession extends ImSessionContext implements Serializable {

    private static final long serialVersionUID = 6077020620501316538L;

    private Map<String, Serializable> data = new ConcurrentHashMap<>();

    private String id = null;

    public HttpSession() {
    }

    /**
     * @author wchao
     */
    public HttpSession(String id) {
        this.id = id;
    }

    /**
     * 清空所有属性
     *
     * @param httpConfig
     * @author wchao
     */
    public void clear(HttpConfig httpConfig) {
        data.clear();
        httpConfig.getSessionStore().put(id, this);
    }

    /**
     * 获取会话属性
     *
     * @param key
     * @return
     * @author wchao
     */
    public Object getAttribute(String key) {
        return data.get(key);
    }

    public Map<String, Serializable> getData() {
        return data;
    }

    /**
     * @param key
     * @param httpConfig
     * @author wchao
     */
    public void removeAttribute(String key, HttpConfig httpConfig) {
        data.remove(key);
        httpConfig.getSessionStore().put(id, this);
    }

    /**
     * 设置会话属性
     *
     * @param key
     * @param value
     * @param httpConfig
     * @author wchao
     */
    public void setAttribute(String key, Serializable value, HttpConfig httpConfig) {
        data.put(key, value);
        httpConfig.getSessionStore().put(id, this);
    }

    public void setData(Map<String, Serializable> data) {
        this.data = data;
    }
}
