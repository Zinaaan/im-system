package com.zinan.im.service.group.model.req;


import com.zinan.im.common.model.RequestBase;
import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/10 19:03
 * @description
 */
@Data
public class SendGroupMessageReq extends RequestBase {

    /**
     * Message id sent by client
     */
    private String messageId;

    private String fromId;

    private String groupId;

    private int messageRandom;

    private long messageTime;

    private String messageBody;

    /**
     * This field defaults to either 0 for a count or 1 for no count for this message, i.e. the number of the icon in the upper right corner does not increase
     */
    private int badgeMode;

    private Long messageLifeTime;

    private Integer appId;

}
