package com.happyheng.lazy.service;

import java.lang.reflect.Method;

/**
 * 延迟执行服务类
 * Created by happyheng on 2019-10-04.
 */
public interface LazyExecutionService {

    /**
     * 延迟执行
     * @param method    延迟执行的方法
     * @param params    对应方法的参数
     * @param lazyTime  延迟执行的时间，以毫秒为单位
     */
    void lazyExecute(Method method, Object[] params, long lazyTime) throws Exception;

}
