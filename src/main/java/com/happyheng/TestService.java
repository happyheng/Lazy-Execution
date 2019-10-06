package com.happyheng;

import com.happyheng.lazy.anno.LazyExecution;
import org.springframework.stereotype.Service;

/**
 * Created by happyheng on 2019-10-06.
 */
@Service
public class TestService {

    @LazyExecution(lazyTime = 5000)
    public void testLazyExecute(Integer value) {
        System.out.println("延时执行，value："+value);
    }

}
