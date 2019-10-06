package com.happyheng.lazy.mq;

import com.google.gson.Gson;
import com.happyheng.lazy.bean.SerializableMethod;
import com.happyheng.lazy.bean.UnSerializableExecuteAbleMethod;
import com.happyheng.lazy.service.LazySerializeMethodService;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * Created by happyheng on 2019-10-05.
 */
@Component
public class LazyExecutionConsumer {

    @Value("${rabbitmq.dead.queue.name}")
    private String deadQueueName;
    @Autowired
    private RabbitMQConnectionFactoryHolder rabbitMQConnectionFactoryHolder;
    @Autowired
    private LazySerializeMethodService lazySerializeMethodService;


    @PostConstruct
    public void consumer() throws Exception{

        // 获取到连接与channel
        Connection connection = rabbitMQConnectionFactoryHolder.getConnectionFactory().newConnection();
        Channel channel = connection.createChannel();

        // 设置qos为10
        channel.basicQos(10);
        // 监听死信队列，设置需要手动确认，传入consumer
        channel.basicConsume(deadQueueName, false, "consumer-tag", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                // consumer中获取到消息后，
                String deliveryContent = new String(body);
                System.out.println("消费者收到消息 deliveryContent:" + deliveryContent);
                try {
                    // 将内容反序列化成 SerializableMethod
                    Gson gson = new Gson();

                    SerializableMethod serializableMethod = gson.fromJson(deliveryContent, SerializableMethod.class);
                    // 首先先判断时间是否对的上，与当前时间不要超过10s，否则报错误日志
                    long executeTime = serializableMethod.getExecuteTime();
                    if (System.currentTimeMillis() - executeTime > 10 * 1000) {
                        System.out.println("任务已超时10s deliveryContent：" + deliveryContent);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                        return;
                    }
                    // 调用lazySerializeMethodService获取到反序列化后的可执行bean UnSerializableExecuteAbleMethod
                    UnSerializableExecuteAbleMethod unSerializableExecuteAbleMethod = lazySerializeMethodService.unSerializeMethod(serializableMethod);
                    Object executeObject = unSerializableExecuteAbleMethod.getExecuteObject();
                    Method method = unSerializableExecuteAbleMethod.getMethod();
                    List<Object> methodParamsList = unSerializableExecuteAbleMethod.getParamsList();
                    // 执行
                    method.invoke(executeObject, methodParamsList.toArray(new Object[]{}));
                    System.out.println("消费者执行完成");
                    // ack
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("消费异常，body为 " + deliveryContent);
                }

            }
        });


    }


}
