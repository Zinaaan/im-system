package com.zinan.im.codec;

import com.alibaba.fastjson.JSONObject;
import com.zinan.im.codec.protocols.Message;
import com.zinan.im.codec.protocols.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author lzn
 * @date 2023/06/21 16:06
 * @description Customized message decoder
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.readableBytes() < 28) {
            return;
        }

        //Request Header -> command, version, clientType, messageType, appId, imeiLength, bodyLength
        int command = byteBuf.readInt();
        int version = byteBuf.readInt();
        int clientType = byteBuf.readInt();
        int messageType = byteBuf.readInt();
        int appId = byteBuf.readInt();
        int imeLength = byteBuf.readInt();
        int bodyLength = byteBuf.readInt();

        // Handle sticky packet/unpacking issues
        if(byteBuf.readableBytes() < bodyLength + imeLength){
            byteBuf.resetReaderIndex();
            return;
        }

        // Got the ime data
        byte[] imeBytes = new byte[imeLength];
        byteBuf.readBytes(imeBytes);
        String imeData = new String(imeBytes);

        // Got the body data
        byte[] bodyBytes = new byte[imeLength];
        byteBuf.readBytes(bodyBytes);


        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setCommand(command);
        messageHeader.setVersion(version);
        messageHeader.setClientType(clientType);
        messageHeader.setMessageType(messageType);
        messageHeader.setAppId(appId);
        messageHeader.setImeLength(imeLength);
        messageHeader.setBodyLength(bodyLength);
        Message message = new Message();
        message.setMessageHeader(messageHeader);

        // If the message type sent by the client is JSON
        if(messageType == 0x0){
            String bodyData = new String(bodyBytes);
            JSONObject body = (JSONObject) JSONObject.parse(bodyData);
            message.setMessagePack(body);
        }

        byteBuf.markReaderIndex();
        list.add(message);
    }
}
