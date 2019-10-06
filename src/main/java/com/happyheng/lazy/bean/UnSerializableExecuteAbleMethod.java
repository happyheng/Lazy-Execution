package com.happyheng.lazy.bean;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 可执行的method以及对应参数
 * Created by happyheng on 2019-10-06.
 */
public class UnSerializableExecuteAbleMethod {

    private Object executeObject;

    private Method method;

    private List<Object> paramsList;


    public Object getExecuteObject() {
        return executeObject;
    }

    public UnSerializableExecuteAbleMethod executeObject(Object executeObject) {
        this.executeObject = executeObject;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public UnSerializableExecuteAbleMethod method(Method method) {
        this.method = method;
        return this;
    }

    public List<Object> getParamsList() {
        return paramsList;
    }

    public UnSerializableExecuteAbleMethod paramsList(List<Object> paramsList) {
        this.paramsList = paramsList;
        return this;
    }
}
