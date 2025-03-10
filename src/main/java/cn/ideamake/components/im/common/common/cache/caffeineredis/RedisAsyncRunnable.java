package cn.ideamake.components.im.common.common.cache.caffeineredis;

import cn.ideamake.components.im.common.common.cache.CacheChangeType;
import cn.ideamake.components.im.common.common.cache.CacheChangedVo;
import cn.ideamake.components.im.common.common.cache.caffeineredis.CaffeineRedisCacheManager;
import cn.ideamake.components.im.common.common.cache.caffeineredis.RedisL2Vo;
import cn.ideamake.components.im.common.common.cache.redis.JedisTemplate;
import cn.ideamake.components.im.common.common.cache.redis.JedisTemplate.Pair;
import cn.ideamake.components.im.common.common.cache.redis.RedisCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author WChao
 * @date 2018年3月13日 下午7:59:20
 */
@SuppressWarnings("static-access")
public class RedisAsyncRunnable implements Runnable {

    private LinkedBlockingQueue<RedisL2Vo> redisL2VoQueue = new LinkedBlockingQueue<RedisL2Vo>();
    private static boolean started = false;
    private Logger LOG = LoggerFactory.getLogger(cn.ideamake.components.im.common.common.cache.caffeineredis.RedisAsyncRunnable.class);

    public void add(RedisL2Vo redisL2Vo) {
        this.redisL2VoQueue.offer(redisL2Vo);
    }

    @Override
    public void run() {
        if (started) {
            return;
        }
        synchronized (cn.ideamake.components.im.common.common.cache.caffeineredis.RedisAsyncRunnable.class) {
            if (started) {
                return;
            }
            started = true;
        }
        Map<String, List<Pair<String, Serializable>>> pairMap = new HashMap<String, List<Pair<String, Serializable>>>();
        List<String> cacheChangeVos = new ArrayList<String>();
        int count = 0;
        while (true) {
            try {
                RedisL2Vo redisL2Vo = redisL2VoQueue.poll();
                if (redisL2Vo != null) {
                    String cacheName = redisL2Vo.getRedisCache().getCacheName();
                    if (pairMap.get(cacheName) == null) {
                        List<Pair<String, Serializable>> pairDatas = new ArrayList<Pair<String, Serializable>>();
                        pairMap.put(cacheName, pairDatas);
                    }
                    pairMap.get(cacheName).add(JedisTemplate.me().makePair(redisL2Vo.getKey(), redisL2Vo.getValue()));
                    cacheChangeVos.add(new CacheChangedVo(redisL2Vo.getRedisCache().getCacheName(), redisL2Vo.getKey(), CacheChangeType.PUT).toString());
                    count++;
                }
                if (count > 0 && redisL2Vo == null) {//队列数据为空
                    for (String cacheName : pairMap.keySet()) {
                        RedisCacheManager.getCache(cacheName).putAll(pairMap.get(cacheName));
                    }
                    JedisTemplate.me().publishAll(CaffeineRedisCacheManager.CACHE_CHANGE_TOPIC, cacheChangeVos);
                    pairMap.clear();
                    cacheChangeVos.clear();
                    count = 0;
                } else if (count == 0 && redisL2Vo == null) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        LOG.error(e.toString(), e);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }
    }
}
