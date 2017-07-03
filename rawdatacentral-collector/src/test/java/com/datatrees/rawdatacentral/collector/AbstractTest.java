package com.datatrees.rawdatacentral.collector;

import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@SpringBootConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@ImportResource(locations={ "test.spring.xml"})
public abstract class AbstractTest {

}
