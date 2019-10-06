package com.happyheng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Created by happyheng on 2019-10-04.
 */
@RequestMapping("/mvc")
@RestController
public class TestMvc {

    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    public String test(HttpServletRequest request) {
        testService.testLazyExecute(1000);
        return "TestMvc";
    }

}
