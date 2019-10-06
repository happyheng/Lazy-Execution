package com.happyheng.lazy.service.impl;

import com.google.gson.Gson;
import com.happyheng.lazy.bean.SerializableMethod;
import com.happyheng.lazy.mq.ProducerConnectionHolder;
import com.happyheng.lazy.service.LazyExecutionService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * rabbitMQ延迟执行的实现类
 * Created by happyheng on 2019-10-04.
 */
@Service
public class RabbitMQLazyExecutionServiceImpl implements LazyExecutionService {

    @Value("${rabbitmq.lazy.exchange.name}")
    private String exchangeName;
    @Autowired
    private ProducerConnectionHolder producerConnectionHolder;

    @Override
    public void lazyExecute(Method method, Object[] params, long lazyTime) throws Exception{

        // 找到对应的class的全名
        Class<?> executeClass = method.getDeclaringClass();
        String className = executeClass.getName();
        // 找到method的名称
        String methodName = method.getName();
        // 找到method的参数列表的class全名数组
        List<String> paramsTypeClassName = null;
        if (method.getParameterTypes().length >0) {
            paramsTypeClassName =  Stream.of(method.getParameterTypes())
                    .map(paramsType -> paramsType.getName())
                    .collect(Collectors.toList());
        }

        // 将params序列化成字符串列表，使用Gson
        List<String> serializableParams = null;
        final Gson gson = new Gson();
        if (params != null && params.length > 0) {
            serializableParams =  Stream.of(params)
                    .map(param ->gson.toJson(param))
                    .collect(Collectors.toList());
        }
        // 得到最终执行的时间，注意是 当前时间System.currentTimestamps+lazyTime
        long executeTime = System.currentTimeMillis() + lazyTime;
        // 生成 SerializableMethod ,并将其进行序列化成功json
        SerializableMethod serializableMethod = new SerializableMethod()
                .className(className)
                .methodName(methodName)
                .paramsTypeClassName(paramsTypeClassName)
                .params(serializableParams)
                .executeTime(executeTime);
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
