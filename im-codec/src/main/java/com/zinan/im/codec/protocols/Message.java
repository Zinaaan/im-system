package com.zinan.im.codec.protocols;

import lombok.Data;

/**
 * Customized message entity for data transmission
 *
 * @author lzn
 * @date 2023/06/21 16:19
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messagePack;
}
