package com.happyheng;

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


    @RequestMapping("/test")
    public String test(HttpServletRequest request) {

        return "TestMvc";
    }

}
