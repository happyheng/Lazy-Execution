package com.happyheng.lazy.bean;

import java.util.List;

/**
 * 方法执行序列化bean
 * Created by happyheng on 2019-10-04.
 */
public class SerializableMethod {

    /**
     * class的名称
     */
    private String className;

    /**
     * 方法的名称
     */
    private String methodName;

    /**
     * 方法的参数类型名称数组
     */
    private List<String> paramsTypeClassName;

    /**
     * 序列化后的执行参数数组
     */
    private List<String> params;

    /**
     * 准确的执行时间，以毫秒为单位
     */
    private long executeTime;

    public String getClassName() {
        return className;
    }

    public SerializableMethod className(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public SerializableMethod methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }


    public List<String> getParamsTypeClassName() {
        return paramsTypeClassName;
    }

    public SerializableMethod paramsTypeClassName(List<String> paramsTypeClassName) {
        this.paramsTypeClassName = paramsTypeClassName;
        return this;
    }

    public List<String> getParams() {
        return params;
    }

    public SerializableMethod params(List<String> params) {
        this.params = params;
        return this;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public SerializableMethod executeTime(long executeTime) {
        this.executeTime = executeTime;
        return this;
    }
}
