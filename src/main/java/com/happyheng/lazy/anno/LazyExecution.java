package com.happyheng.lazy.anno;

import java.lang.annotation.*;

/**
 *
 * Created by happyheng on 2019-10-04.
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LazyExecution {

    /**
     * 延迟时间，毫秒为单位
     */
    long lazyTime() default 0;

}
