package com.happyheng.lazy.service;

import com.google.gson.Gson;
import com.happyheng.lazy.bean.SerializableMethod;
import com.happyheng.lazy.bean.UnSerializableExecuteAbleMethod;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法序列化与反序列化Service
 * Created by happyheng on 2019-10-06.
 */
@Service
public class LazySerializeMethodService implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext var1) throws BeansException {
        this.applicationContext = var1;
    }

    /**
     * 将制定方法与方法的参数序列化成 SerializableMethod bean
     * @param method        方法对应的method
     * @param params        方法传入的参数列表
     * @param lazyTime      延迟时间
     * @return  SerializableMethod 序列化bean
     */
    public SerializableMethod serializeMethod(Method method, Object[] params, long lazyTime) {
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
        return new SerializableMethod()
                .className(className)
                .methodName(methodName)
                .paramsTypeClassName(paramsTypeClassName)
                .params(serializableParams)
                .executeTime(executeTime);
    }


    public UnSerializableExecuteAbleMethod unSerializeMethod(SerializableMethod serializableMethod) throws Exception{

        // 获取到里面的 className， methodName，方法的参数类型Name列表
        Gson gson = new Gson();
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
        UnSerializableExecuteAbleMethod unSerializableExecuteAbleMethod = new UnSerializableExecuteAbleMethod();
        return unSerializableExecuteAbleMethod.executeObject(executeObject)
                .method(method)
                .paramsList(methodParamsList);

    }

}
