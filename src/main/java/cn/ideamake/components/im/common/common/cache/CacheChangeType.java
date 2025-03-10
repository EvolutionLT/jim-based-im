package cn.ideamake.components.im.common.common.cache;

import java.util.Objects;

/**
 * @author WChao
 * 2017年8月12日 下午9:33:02
 */
public enum CacheChangeType {
    /**
     * key级别清空本地缓存
     */
    REMOVE(1),
    /**
     * key级别清空本地缓存
     */
    UPDATE(2),
    /**
     * key级别清空本地缓存
     */
    PUT(3),
    /**
     * cacheName级别清空本地缓存
     */
    CLEAR(4);

    public static cn.ideamake.components.im.common.common.cache.CacheChangeType from(Integer method) {
        cn.ideamake.components.im.common.common.cache.CacheChangeType[] values = cn.ideamake.components.im.common.common.cache.CacheChangeType.values();
        for (cn.ideamake.components.im.common.common.cache.CacheChangeType v : values) {
            if (Objects.equals(v.value, method)) {
                return v;
            }
        }
        return null;
    }

    Integer value;

    private CacheChangeType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
