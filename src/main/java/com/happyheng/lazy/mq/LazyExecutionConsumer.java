package com.happyheng.lazy.mq;

import com.google.gson.Gson;
import com.happyheng.lazy.bean.SerializableMethod;
import com.rabbitmq.client.*;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by happyheng on 2019-10-05.
 */
@Component
public class LazyExecutionConsumer implements ApplicationContextAware {

    @Value("${rabbitmq.dead.queue.name}")
    private String deadQueueName;
    @Autowired
    private RabbitMQConnectionFactoryHolder rabbitMQConnectionFactoryHolder;

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext var1) throws BeansException {
        this.applicationContext = var1;
    }

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

                    // 获取到里面的 className， methodName，方法的参数类型Name列表
                    String className = serializableMethod.getClassName();
                    String methodName = serializableMethod.getMethodName();
                    List<String> paramsTypeClassNameList = serializableMethod.getParamsTypeClassName();
                    List<String> params = serializableMethod.getParams();
                    // 找到对应的object
                    Class<?> executeObjectClass = Class.forName(className);
                    Object executeProxyObject = applicationContext.getBean(executeObjectClass);
                    Object executeObject = ((Advised)executeProxyObject).getTargetSource().getTarget();

                    // 根据参数序列化的数组与method中参数类型，得到执行参数中的类型数组，与json参数数组反序列化成object数组
                    List<Class<?>> methodParamsTypeList = new ArrayList<>();
                    List<Object> methodParamsList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(paramsTypeClassNameList)) {
                        methodParamsTypeList = paramsTypeClassNameList.stream()
                                .map(paramsTypeClassName -> {
                                    try {
                                        return Class.forName(paramsTypeClassName);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }).collect(Collectors.toList());

                        for (int i=0; i< paramsTypeClassNameList.size(); i ++) {
                            methodParamsList.add(gson.fromJson(params.get(i),
                                    Class.forName(paramsTypeClassNameList.get(i))));

                        }
                    }

                    // 找到method
                    Method method = executeObjectClass.getMethod(methodName, methodParamsTypeList.toArray(new Class<?>[]{}));

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
