package com.datatrees.rawdatacentral.service;

import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by zhouxinghai on 2017/6/27.
 */
@SpringBootConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@ImportResource(locations={ "rawdatacentral-service.xml"})
public class BaseTest {
}
