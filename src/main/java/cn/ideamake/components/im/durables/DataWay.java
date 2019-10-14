package cn.ideamake.components.im.durables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author evolution
 * @title: DataWay
 * @projectName im
 * @description: TODO 注解 用于区分数据持久化数据的类型 0 mysql  1 mongDB  2 tablestorm
 * @date 2019-07-07 11:24
 * @ltd：思为
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataWay {

    int value();
}
