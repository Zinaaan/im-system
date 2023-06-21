package com.zinan.im.codec.protocols;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/21 16:19
 * @description
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messageData;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messageData=" + messageData +
                '}';
    }
}
