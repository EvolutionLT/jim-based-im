package cn.ideamake.components.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author evolution
 * @since 2019-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_im_im_record")
public class IMChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * 消息ID  32uuid
     */
    private String msgId;

    /**
     * 消息发送人
     */
    private String targetId;

    /**
     * 消息接收人
     */
    private String fromId;

    /**
     *消息类型 0: 文本、1:图片、2: 语音、3:视频、4:名片信息、5:楼盘信息
     */
    private String msgType;

    /**
     *消息内容
     */
    private String msgContent;

    /**
     *消息创建时间
     */
    private String msgCreateTime;

    /**
     *消息是否已读 0-未读，1-已读
     */
    private Integer isRead;


    /**
     * 是否发送为置业顾问
     */
     private Integer isToUser;

    /**
     * 接受方是否为顾问
     */
    private Integer isFromUser;

    /**
     * 是否自动发送，0-不，1-是
     */
    private Integer isAutoSend;

    /**
     * 额外场景
     */
    private String extras;

    /**
     * 聊天场景
     */
    private String chatScene;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updateAt;


    /**
     * json字符串方式存储
     */
    private String jsonContent;

    /**
     * 房间ID(分组ID)
     */
    private String roomId;

    /**
     * 0 在线消息 1 离线消息
     */
    private String isOnline;

}
