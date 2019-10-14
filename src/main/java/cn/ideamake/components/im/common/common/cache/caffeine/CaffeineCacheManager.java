/**
 *
 */
package cn.ideamake.components.im.common.common.cache.caffeine;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import cn.ideamake.components.im.common.common.cache.caffeine.CaffeineCache;
import cn.ideamake.components.im.common.common.cache.caffeine.CaffeineConfiguration;
import cn.ideamake.components.im.common.common.cache.caffeine.CaffeineConfigurationFactory;
import cn.ideamake.components.im.common.common.cache.caffeine.CaffeineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author WChao
 * @date 2018年3月9日 上午12:54:07
 */
public class CaffeineCacheManager {

    private static Map<String, CaffeineCache> map = new HashMap<String, CaffeineCache>();


    private static Logger log = LoggerFactory.getLogger(cn.ideamake.components.im.common.common.cache.caffeine.CaffeineCacheManager.class);

    private CaffeineCacheManager() {
    }

    static {
        try {
            List<CaffeineConfiguration> configurations = CaffeineConfigurationFactory.parseConfiguration();
            for (CaffeineConfiguration configuration : configurations) {
                register(configuration.getCacheName(), configuration.getTimeToLiveSeconds(), configuration.getTimeToIdleSeconds(), configuration.getMaximumSize(), null);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static CaffeineCache getCache(String cacheName, boolean skipNull) {
        CaffeineCache CaffeineCache = map.get(cacheName);
        if (CaffeineCache == null && !skipNull) {
            log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)" , cacheName, cn.ideamake.components.im.common.common.cache.caffeine.CaffeineCache.class.getSimpleName());
        }
        return CaffeineCache;
    }

    public static CaffeineCache getCache(String cacheName) {
        return getCache(cacheName, false);
    }

    /**
     * timeToLiveSeconds和timeToIdleSeconds不允许同时为null
     * @param cacheName
     * @param timeToLiveSeconds
     * @param timeToIdleSeconds
     * @return
     * @author wchao
     */
    public static CaffeineCache register(String cacheName, Integer timeToLiveSeconds, Integer timeToIdleSeconds) {
        CaffeineCache CaffeineCache = register(cacheName, timeToLiveSeconds, timeToIdleSeconds, 5000000, null);
        return CaffeineCache;
    }

    public static CaffeineCache register(String cacheName, Integer timeToLiveSeconds, Integer timeToIdleSeconds, Integer maximumSize, RemovalListener<String, Serializable> removalListener) {
        CaffeineCache caffeineCache = map.get(cacheName);
        if (caffeineCache == null) {
            synchronized (cn.ideamake.components.im.common.common.cache.caffeine.CaffeineCacheManager.class) {
                caffeineCache = map.get(cacheName);
                if (caffeineCache == null) {
                    Integer initialCapacity = 10;
                    boolean recordStats = false;
                    LoadingCache<String, Serializable> loadingCache = CaffeineUtils.createLoadingCache(cacheName, timeToLiveSeconds, timeToIdleSeconds, initialCapacity, maximumSize, recordStats, removalListener);
                    LoadingCache<String, Serializable> temporaryLoadingCache = CaffeineUtils.createLoadingCache(cacheName, null, 10, initialCapacity, maximumSize, recordStats, removalListener);
                    caffeineCache = new CaffeineCache(loadingCache, temporaryLoadingCache);
                    map.put(cacheName, caffeineCache);
                }
            }
        }
        return caffeineCache;
    }
}
