package com.zinan.im.tcp.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.zinan.im.common.constant.Constants;
import com.zinan.im.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author lzn
 * @date 2023/07/11 22:05
 * @description Message receiver for Rabbit mq
 */
@Slf4j
public class MessageReceiver {

    private static void startReceiveMessage() {
        try {
            Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MESSAGE_SERVICE_TO_IM);
            channel.queueDeclare(Constants.RabbitConstants.MESSAGE_SERVICE_TO_IM, true, false, false, null);
            channel.queueBind(Constants.RabbitConstants.MESSAGE_SERVICE_TO_IM, Constants.RabbitConstants.MESSAGE_SERVICE_TO_IM, "");
            channel.basicConsume(Constants.RabbitConstants.MESSAGE_SERVICE_TO_IM, false, new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    // Handle received message
                    String message = new String(body);
                    log.info("handle received message:" + message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static void init(){
        startReceiveMessage();
    }
}
