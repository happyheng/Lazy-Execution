package com.happyheng.lazy;

import com.happyheng.lazy.anno.LazyExecution;
import com.happyheng.lazy.service.LazyExecutionService;
import com.happyheng.lazy.service.LazyExecutionServiceFactory;
import com.happyheng.lazy.service.LazyExecutionType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by happyheng on 2019-10-04.
 */
@Component
@Aspect
public class LazyExecutionAspect {

    @Autowired
    private LazyExecutionServiceFactory lazyExecutionServiceFactory;

    @Around("@annotation(lazyExecution)")
    public Object doDistributedLock(final ProceedingJoinPoint point,
                                    LazyExecution lazyExecution) throws Exception{

        // 找到相应的method
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        // 找到执行时间
        long lazyTime = lazyExecution.lazyTime();
        // 调用ServiceFactory得到service并执行
        LazyExecutionService lazyExecutionService = lazyExecutionServiceFactory.getService(LazyExecutionType.RABBIT_MQ);
        lazyExecutionService.lazyExecute(method, point.getArgs(), lazyTime);
        // 直接返回
        return null;
    }


}
