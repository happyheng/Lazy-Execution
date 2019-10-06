package com.happyheng.lazy.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by happyheng on 2019-10-04.
 */
@Component
public class RabbitMqInitService {

    @Value("${rabbitmq.dead.exchange.name}")
    private String deadExchangeName;
    @Value("${rabbitmq.dead.queue.name}")
    private String deadQueueName;
    @Value("${rabbitmq.lazy.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.lazy.queue.name}")
    private String queueName;

    @Autowired
    private ProducerConnectionHolder producerConnectionHolder;

    @PostConstruct
    public void init() throws Exception{
        // 获取连接
        Connection connection = producerConnectionHolder.getProducerConnection();
        Channel channel = connection.createChannel();
        // 生成死信交换器，死信queue
        channel.exchangeDeclare(deadExchangeName, "fanout", true, false, null);
        channel.queueDeclare(deadQueueName, true, false, false, null);
        channel.queueBind(deadQueueName, deadExchangeName, "");
        // 生成延迟交换器、延迟queue（延迟queue要设置死信队列）
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", deadExchangeName);
        channel.exchangeDeclare(exchangeName, "fanout", true, false, false, null);
        channel.queueDeclare(queueName, true, false,false, arguments);
        channel.queueBind(queueName, exchangeName, "");
        channel.close();
    }

}
