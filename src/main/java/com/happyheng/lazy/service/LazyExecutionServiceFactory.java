package com.happyheng.lazy.service;

import com.happyheng.lazy.service.impl.LocalThreadLazyExecutionServiceImpl;
import com.happyheng.lazy.service.impl.RabbitMQLazyExecutionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * Created by happyheng on 2019-10-04.
 */
@Service
public class LazyExecutionServiceFactory {

    @Autowired
    private LocalThreadLazyExecutionServiceImpl localThreadLazyExecutionService;
    @Autowired
    private RabbitMQLazyExecutionServiceImpl rabbitMQLazyExecutionService;

    public LazyExecutionService getService(LazyExecutionType lazyExecutionType) {

        if (lazyExecutionType == null) {
            return null;
        }
        switch (lazyExecutionType) {
            case LOCAL_THREAD:
                return localThreadLazyExecutionService;
            case RABBIT_MQ:
                return rabbitMQLazyExecutionService;
        }
        return null;
    }

}
