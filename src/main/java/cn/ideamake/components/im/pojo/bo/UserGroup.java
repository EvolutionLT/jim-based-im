package cn.ideamake.components.im.pojo.bo;

import lombok.Data;

/**
 * @author Walt
 * @date 2019-09-09 20:45
 * 用户组，使用非关系存储
 */
@Data
public class UserGroup {

    private String groupName;


    public static void main(String[] args) {
        System.out.println(System.nanoTime());
    }
}
