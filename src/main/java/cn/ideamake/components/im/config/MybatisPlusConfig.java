package cn.ideamake.components.im.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author yangdi
 * @date 2019-07-01 5:37 PM
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 分页插件
     * @return  PaginationInterceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptorMysql() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("mysql");
        return page;
    }
    /**
     * SQL执行效率插件 设置 local 环境开启
     * @return PerformanceInterceptor
     */
    @Bean
//    @Profile({"dev", "local"})
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor interceptor = new PerformanceInterceptor();
        //interceptor.setFormat(true);
        interceptor.setMaxTime(10000);
        return interceptor;
    }
}
