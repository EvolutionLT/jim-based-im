package cn.ideamake.components.im.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Walt
 * @date 2019-09-12 14:32
 */
@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //1.配置跨域处理拦截器
        registry.addInterceptor(new CORSSignatureInterceptor()).addPathPatterns("/**");
//        //2.配置鉴权处理拦截器
//        registry.addInterceptor(authTokenInterceptor()).addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/authorization/login/**",
//                        "/articles/{aid}/information",
//                        "/articles/templates/**",
//                        "/static/**",
//                        "/common/**");

    }
}