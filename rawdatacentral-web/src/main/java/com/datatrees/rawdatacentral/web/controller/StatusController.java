package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
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

    @RequestMapping("/status2")
    public Object status2() throws InterruptedException {
        while (true) {
            String websiteName = "zhe_jiang_10086_web";
            OperatorPluginService pluginService = classLoaderService.getOperatorPluginService(websiteName);
            TimeUnit.SECONDS.sleep(1);

        }
    }

}
