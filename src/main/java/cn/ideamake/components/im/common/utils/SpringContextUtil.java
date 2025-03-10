package cn.ideamake.components.im.common.utils;

/**
 * @author evolution
 * @title: SpringContextUtil
 * @projectName cs-based-im
 * @description: TODO
 * @date 2019-09-24 20:32
 * @ltd：思为
 */
import org.springframework.context.ApplicationContext;

public class SpringContextUtil{
    private static ApplicationContext applicationContext;

    public SpringContextUtil() {
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static Object getBean(Class requiredType) {
        return applicationContext.getBean(requiredType);
    }
}