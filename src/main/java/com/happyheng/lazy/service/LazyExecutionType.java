package com.happyheng.lazy.service;

/**
 * 延迟执行的类型
 * Created by happyheng on 2019-10-04.
 */
public enum LazyExecutionType {

    /**
     * 本地线程延迟执行
     */
    LOCAL_THREAD(0),
    /**
     * RabbitMQ延迟执行
     */
    RABBIT_MQ(1),
    ;

    private int value;

    LazyExecutionType(int value) {
        this.value = value;
    }
}
