package com.zinan.im.codec;

import com.alibaba.fastjson.JSONObject;
import com.zinan.im.codec.protocols.Message;
import com.zinan.im.codec.protocols.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author lzn
 * @date 2023/06/21 16:06
 * @description Customized message decoder
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client connected: " + ctx.channel().remoteAddress());
        log.info("Client connected: " + ctx.channel().pipeline());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught: " + cause.getMessage());
        ctx.close();
    }

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
        int pwdLength = byteBuf.readInt();
        int imeLength = byteBuf.readInt();
        int bodyLength = byteBuf.readInt();

        // Handle sticky packet/unpacking issues
        if (byteBuf.readableBytes() < pwdLength + bodyLength + imeLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        // Got password
        byte[] pwdBytes = new byte[pwdLength];
        byteBuf.readBytes(pwdBytes);
        String encryptedPassword = new String(pwdBytes);
        // Compare encryptedPassword with the password in the database for authentication

        // Got ime data
        byte[] imeBytes = new byte[imeLength];
        byteBuf.readBytes(imeBytes);
        String imeData = new String(imeBytes);

        // Got body data
        byte[] bodyBytes = new byte[bodyLength];
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
        if (messageType == 0x0) {
            String bodyData = new String(bodyBytes);
            JSONObject body = (JSONObject) JSONObject.parse(bodyData);
            message.setMessagePack(body);
        }

        byteBuf.markReaderIndex();
        list.add(message);
    }
}
