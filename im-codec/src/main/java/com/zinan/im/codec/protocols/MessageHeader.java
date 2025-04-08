package com.zinan.im.codec.protocols;

import lombok.Data;

/**
 * Customized message header for message entity
 *
 * @author lzn
 * @date 2023/06/21 16:18
 */
@Data
public class MessageHeader {

    /**
     * RequestType
     */
    private Integer command;
    private Integer version;
    private Integer clientType;
    /**
     * 0x0 by default. 0x0 -> JSON, 0x1 -> Protobuf, 0x2 -> Xml
     */
    private Integer messageType = 0x0;
    private Integer appId;
    private Integer imeLength;
    private Integer bodyLength;
}
