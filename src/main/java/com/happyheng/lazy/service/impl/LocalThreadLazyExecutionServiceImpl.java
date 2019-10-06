package com.happyheng.lazy.service.impl;

import com.happyheng.lazy.service.LazyExecutionService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 本地线程延迟执行的服务实现类
 * Created by happyheng on 2019-10-04.
 */
@Service
public class LocalThreadLazyExecutionServiceImpl implements LazyExecutionService {

    @Override
    public void lazyExecute(Method method, Object[] params, long lazyTime) throws Exception{

    }

}
