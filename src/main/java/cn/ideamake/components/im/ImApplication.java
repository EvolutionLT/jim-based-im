package cn.ideamake.components.im;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import static cn.ideamake.components.im.common.utils.SpringContextUtil.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@Slf4j
@MapperScan("cn.ideamake.components.im.dto.mapper")

public class ImApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(ImApplication.class, args);
        setApplicationContext(app);
       // SpringApplication.run(ImApplication.class, args);
    }


    @Bean
    public Executor vankeExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //此方法返回可用处理器的虚拟机的最大数量; 不小于1
        int core = Runtime.getRuntime().availableProcessors();
        //设置核心线程数
        executor.setCorePoolSize(core);
        //设置最大线程数
        executor.setMaxPoolSize(core*2 + 1);
        //除核心线程外的线程存活时间
        executor.setKeepAliveSeconds(5);
        //如果传入值大于0，底层队列使用的是LinkedBlockingQueue,否则默认使用SynchronousQueue
        executor.setQueueCapacity(600);
        //线程名称前缀
        executor.setThreadNamePrefix("vanke-write-message-execute");
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
