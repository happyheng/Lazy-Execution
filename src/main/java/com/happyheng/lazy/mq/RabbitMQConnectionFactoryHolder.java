package com.happyheng.lazy.mq;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by happyheng on 2019-10-05.
 */
@Component
public class RabbitMQConnectionFactoryHolder {

    @Value("${rabbitmq.ip}")
    private String ip;
    @Value("${rabbitmq.port}")
    private int port;
    @Value("${rabbitmq.username}")
    private String userName;
    @Value("${rabbitmq.password}")
    private String password;

    private ConnectionFactory connectionFactory;
    private final Object LOCK = new Object();

    public ConnectionFactory getConnectionFactory() {
        if (connectionFactory != null) {
            return connectionFactory;
        }
        synchronized (LOCK) {
            if (connectionFactory != null) {
                return connectionFactory;
            }
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(ip);
            connectionFactory.setPort(port);
            connectionFactory.setUsername(userName);
            connectionFactory.setPassword(password);
        }
        return connectionFactory;
    }

}
