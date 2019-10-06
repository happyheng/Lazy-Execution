package com.happyheng.lazy.service.impl;

import com.google.gson.Gson;
import com.happyheng.lazy.bean.SerializableMethod;
import com.happyheng.lazy.mq.ProducerConnectionHolder;
import com.happyheng.lazy.service.LazyExecutionService;
import com.happyheng.lazy.service.LazySerializeMethodService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * rabbitMQ延迟执行的实现类
 * Created by happyheng on 2019-10-04.
 */
@Service
public class RabbitMQLazyExecutionServiceImpl implements LazyExecutionService {

    @Value("${rabbitmq.lazy.exchange.name}")
    private String exchangeName;
    @Autowired
    private LazySerializeMethodService lazySerializeMethodService;
    @Autowired
    private ProducerConnectionHolder producerConnectionHolder;



    @Override
    public void lazyExecute(Method method, Object[] params, long lazyTime) throws Exception{
        // 生成 SerializableMethod ，将method以及对应执行的参数都序列化
        SerializableMethod serializableMethod = lazySerializeMethodService.serializeMethod(method, params, lazyTime);
        // 调用mq进行发送
        sendToMq(serializableMethod, lazyTime);
    }

    private void sendToMq(SerializableMethod serializableMethod, long lazyTime) throws Exception{
        Gson gson = new Gson();
        Connection connection = producerConnectionHolder.getProducerConnection();
        Channel channel = connection.createChannel();
        AMQP.BasicProperties.Builder propertiesBuilder = new AMQP.BasicProperties.Builder();
        propertiesBuilder.deliveryMode(2);
        propertiesBuilder.expiration(String.valueOf(lazyTime));
        channel.basicPublish(exchangeName, "", propertiesBuilder.build(), gson.toJson(serializableMethod).getBytes());
        channel.close();
    }
}
