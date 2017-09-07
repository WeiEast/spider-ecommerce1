package com.datatrees.rawdatacentral.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
