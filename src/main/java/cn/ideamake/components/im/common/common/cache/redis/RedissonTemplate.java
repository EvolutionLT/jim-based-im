package cn.ideamake.components.im.common.common.cache.redis;

import cn.ideamake.components.im.common.common.cache.redis.RedisConfiguration;
import cn.ideamake.components.im.common.common.cache.redis.RedisConfigurationFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author WChao
 * @date 2018年5月18日 下午2:46:55
 */
public class RedissonTemplate implements Serializable{

	private static final long serialVersionUID = -4528751601700736437L;
	private static final Logger logger = LoggerFactory.getLogger(RedissonTemplate.class);
	private static volatile RedissonTemplate instance = null;
	private static RedisConfiguration redisConfig = null;
	private static final String REDIS = "redis";
	private static RedissonClient redissonClient = null;
	
	private RedissonTemplate(){};
	
	public static RedissonTemplate me() {
		 if (instance == null) { 
	        	synchronized (RedissonTemplate.class) {
					if(instance == null){
						try {
							redisConfig = RedisConfigurationFactory.parseConfiguration();
							init();
						} catch (Exception e) {
							e.printStackTrace();
						}
						instance = new RedissonTemplate();
					}
				}
	     }
		 return instance;
	}
	
	private static final void init() throws Exception {
			String host = redisConfig.getHost();
			if(host == null) {
				logger.error("the server ip of redis  must be not null!");
				throw new Exception("the server ip of redis  must be not null!");
			}	
			int port = redisConfig.getPort();
			String password = redisConfig.getAuth();
			Config redissonConfig = new Config();
			SingleServerConfig singleServerConfig = redissonConfig.useSingleServer();
			singleServerConfig.setAddress(REDIS+"://"+host+":"+port).setPassword(password).setTimeout(redisConfig.getTimeout()).setRetryAttempts(redisConfig.getRetryNum());
			try {
			   redissonClient = Redisson.create(redissonConfig);
			} catch (Exception e) {
				logger.error("cann't create RedissonClient for server"+redisConfig.getHost());
				throw new Exception("cann't create RedissonClient for server"+redisConfig.getHost());
			}
			
	}
	/**
	 * 获取RedissonClient客户端;
	 * @return
	 */
	public final RedissonClient getRedissonClient(){
		return redissonClient;
	}
}
