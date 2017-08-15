package com.datatrees.rawdatacentral.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatusController {

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
