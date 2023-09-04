package com.zinan.im.codec.protocols;

import lombok.Data;

/**
 * @author lzn
 * @date 2023/06/21 16:19
 * @description Customized message entity for data transmission
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messagePack;
}
