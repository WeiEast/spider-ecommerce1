package com.datatrees.rawdatacentral.core.message;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

@ContextConfiguration(locations = {"classpath:/test.spring.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RocketMessageTest {

    
    private String readFile(String path) {
        String content = "";
        InputStream input = ClassLoader.getSystemResourceAsStream(path);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String data;
            while ((data = reader.readLine()) != null)
                content = content + data;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }
    
    @Resource
    private MQProducer producer;
    
    @Test
    public void doTest() throws InterruptedException, MQClientException, RemotingException, MQBrokerException {
        producer.send(new Message("rawData_input", "login_info", "HAHA", readFile("message").getBytes()));
    }

}
