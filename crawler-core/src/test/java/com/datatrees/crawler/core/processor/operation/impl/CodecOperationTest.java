package com.datatrees.crawler.core.processor.operation.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.OperationType;
import com.datatrees.crawler.core.domain.config.operation.impl.CodecOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.codec.CodecType;
import com.datatrees.crawler.core.domain.config.operation.impl.codec.HandlingType;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.operation.BaseOperationTest;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CodecOperationTest extends BaseOperationTest {

    @Ignore
    @Test
    public void Md4Test() {
        String orginal = "22f7961e122079607023be47adcb3821kgcloud";
        byte[] sourceBytes = null;
        try {
            sourceBytes = orginal.getBytes("UTF-8");
            System.out.println(DigestUtils.md5Hex(sourceBytes));
            //			byte[] dest = DigestUtils.md5Hex(sourceBytes);
            //			String result = new String(dest);
            //			System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void testMd5Encode() {
        String input = "22f7961e122079607023be47adcb3821kgcloud";
        CodecOperation op = new CodecOperation();
        op.setCodecType("md5");
        op.setHandlingType("encode");
        op.setType(OperationType.CODEC.getValue());
        try {
            Operation operation = ProcessorFactory.getOperation(op);
            Request req = createDummyRequest(input);
            Response resp = createDummyResponse(null);
            operation.invoke(req, resp);
            String result = "ed75320744200fd19089d35e328053b0";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }
    }

    @Test
    public void testUrlEncode() throws Exception {

        //		System.out.println(URLEncoder.encode("http://static.viooz.co/02/plugins/plugins_player1.php", "UTF-8"));

        String input = "22f7961e122079607023be47adcb3821kgcloud";
        input = "http://static.viooz.co/02/plugins/plugins_player1.php";
        CodecOperation op = new CodecOperation();
        op.setCodecType(CodecType.URI.getValue());
        op.setHandlingType(HandlingType.ENCODE.getValue());
        op.setType(OperationType.CODEC.getValue());
        try {
            Operation operation = ProcessorFactory.getOperation(op);
            Request req = createDummyRequest(input);
            Response resp = createDummyResponse(null);
            operation.invoke(req, resp);
            String result = "MjJmNzk2MWUxMjIwNzk2MDcwMjNiZTQ3YWRjYjM4MjFrZ2Nsb3Vk";
            result = "http%3A%2F%2Fstatic.viooz.co%2F02%2Fplugins%2Fplugins_player1.php";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }
    }

    @Test
    public void testUrlDecode() throws Exception {

        //		System.out.println(URLEncoder.encode("http://static.viooz.co/02/plugins/plugins_player1.php", "UTF-8"));

        String input = "22f7961e122079607023be47adcb3821kgcloud";
        input = "http%3A%2F%2Fstatic.viooz.co%2F02%2Fplugins%2Fplugins_player1.php";
        CodecOperation op = new CodecOperation();
        op.setCodecType(CodecType.URI.getValue());
        op.setHandlingType(HandlingType.DECODE.getValue());
        op.setType(OperationType.CODEC.getValue());
        try {
            Operation operation = ProcessorFactory.getOperation(op);
            Request req = createDummyRequest(input);
            Response resp = createDummyResponse(null);
            operation.invoke(req, resp);
            String result = "MjJmNzk2MWUxMjIwNzk2MDcwMjNiZTQ3YWRjYjM4MjFrZ2Nsb3Vk";
            result = "http://static.viooz.co/02/plugins/plugins_player1.php";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }
    }

    @Ignore
    @Test
    public void testBase64Encode() {
        String input = "22f7961e122079607023be47adcb3821kgcloud";
        input = "22f7961e122079607023be47adcb3821kg爱cloud";
        CodecOperation op = new CodecOperation();
        op.setCodecType(CodecType.BASE64.getValue());
        op.setHandlingType(HandlingType.ENCODE.getValue());
        op.setType(OperationType.CODEC.getValue());
        try {
            Operation operation = ProcessorFactory.getOperation(op);
            Request req = createDummyRequest(input);
            Response resp = createDummyResponse(null);
            operation.invoke(req, resp);
            String result = "MjJmNzk2MWUxMjIwNzk2MDcwMjNiZTQ3YWRjYjM4MjFrZ2Nsb3Vk";
            result = "MjJmNzk2MWUxMjIwNzk2MDcwMjNiZTQ3YWRjYjM4MjFrZ+eIsWNsb3Vk";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }
    }

    @Ignore
    @Test
    public void testBase64Decode() {
        String input = "22f7961e122079607023be47adcb3821kgcloud";
        input = "MjJmNzk2MWUxMjIwNzk2MDcwMjNiZTQ3YWRjYjM4MjFrZ+eIsWNsb3Vk";
        //		input = "22f7961e122079607023be47adcb3821kg爱cloud";
        CodecOperation op = new CodecOperation();
        op.setCodecType(CodecType.BASE64.getValue());
        op.setHandlingType(HandlingType.DECODE.getValue());
        op.setType(OperationType.CODEC.getValue());
        try {
            Operation operation = ProcessorFactory.getOperation(op);
            Request req = createDummyRequest(input);
            Response resp = createDummyResponse(null);
            operation.invoke(req, resp);
            String result = "MjJmNzk2MWUxMjIwNzk2MDcwMjNiZTQ3YWRjYjM4MjFrZ2Nsb3Vk";
            result = "MjJmNzk2MWUxMjIwNzk2MDcwMjNiZTQ3YWRjYjM4MjFrZ+eIsWNsb3Vk";
            result = "22f7961e122079607023be47adcb3821kg爱cloud";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }
    }
}
