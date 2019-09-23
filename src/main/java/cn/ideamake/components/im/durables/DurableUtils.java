package cn.ideamake.components.im.durables;

import cn.ideamake.components.im.common.common.packets.ChatBody;
import org.springframework.stereotype.Component;

/**
 * @author evolution
 * @title: Contexts
 * @projectName im
 * @description: TODO 上下文 根据上下文获取具体实现类
 * @date 2019-07-07 14:35
 * @ltd：思为
 */

@Component
public class DurableUtils {

    private static DurableUtils durableUtils;
    private DataCrudStrategy dataCrudStrategy;

    //@Value("${ideamake.data.channelId}")
    private int channelId=1;
    /**
     * 插入消息记录
     * @param chatBody
     * @return
     */
    public int insertMsgData(ChatBody chatBody) {
        /**
         * 获取该类的实例
         */
        DataStrageryFactory dataStrageryFactory = DataStrageryFactory.getInstance();

        try {
            //传入配置文件中的持久化类型
            dataCrudStrategy = dataStrageryFactory.create(channelId);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return dataCrudStrategy.insertMsgData(chatBody);
    }


    public synchronized static DurableUtils getInstance(){
        if(durableUtils==null){
            durableUtils=new DurableUtils();
        }
        return durableUtils;
    }



}
