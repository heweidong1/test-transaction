package com.luban;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("server1")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "test")
    public void test() {
        demoService.test();
    }
}
