package cn.ideamake.components.im.durables;


import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Set;

/**
 * @author evolution
 * @title: DataStrageryFactory
 * @projectName im
 * @description: TODO  工厂模式  通过注解扫描出需要加载的类
 * @date 2019-07-07 11:29
 * @ltd：思为
 */
@Slf4j
public class DataStrageryFactory {
    /**
     * 定义工厂类
     */
    private static DataStrageryFactory dataStrageryFactory =new DataStrageryFactory();

    /**
     * 定义缓存Map
     */
    public static HashMap<Integer,String> data_map= new HashMap<>();

    /**
     * 静态代码块 类加载器加载后执行一次
     */
    static {
        //根据注解解析出来  存放至Map中 key 就是注解的值  value 就是全路径类名
        Reflections reflections = new Reflections("cn.ideamake.components.im.durables.channel");
        Set<Class<?>> classList=reflections.getTypesAnnotatedWith(DataWay.class);
        for(Class clazz : classList){
            DataWay t=(DataWay)clazz.getAnnotation(DataWay.class);
            data_map.put(t.value(),clazz.getCanonicalName());

        }
    }

    public  DataStrageryFactory(){

    }
    public DataCrudStrategy create(int channelId) throws IllegalAccessException, InstantiationException {
        String clazz=data_map.get(channelId);
        if(clazz==null){
            log.error("暂时支持该数据类型的持久化"+channelId);
        }
        Class clazz_= null;
        try {
            clazz_ = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (DataCrudStrategy) clazz_.newInstance();
    }
    public static DataStrageryFactory getInstance(){

        return  dataStrageryFactory;
    }

}
