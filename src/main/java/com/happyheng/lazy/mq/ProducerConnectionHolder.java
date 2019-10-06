package com.happyheng.lazy.mq;

import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 生产者-rabbitMQ之间的连接
 * Created by happyheng on 2019-10-04.
 */
@Component
public class ProducerConnectionHolder {

    @Autowired
    private RabbitMQConnectionFactoryHolder rabbitMQConnectionFactoryHolder;
    private Connection connection;

    public Connection getProducerConnection() {
        return connection;
    }

    @PostConstruct
    public void init() throws Exception{
        connection = rabbitMQConnectionFactoryHolder.getConnectionFactory().newConnection();
    }

    @PreDestroy
    public void destroy() throws Exception{
        if (connection == null) {
            return;
        }
        connection.close();
    }


}
