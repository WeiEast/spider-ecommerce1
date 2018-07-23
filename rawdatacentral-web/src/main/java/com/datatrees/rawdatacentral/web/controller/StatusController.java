package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.rawdatacentral.service.ClassLoaderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @Resource
    private ClassLoaderService classLoaderService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/status")
    public Object status() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "ok");
        return map;
    }

}
