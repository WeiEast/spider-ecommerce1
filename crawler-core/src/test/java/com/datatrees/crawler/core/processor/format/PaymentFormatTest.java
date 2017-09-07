package com.datatrees.crawler.core.processor.format;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.processor.format.impl.PaymentFormatImpl;
import org.junit.Test;

public class PaymentFormatTest {

    @Test
    public void testPF() {

        PaymentFormatImpl paymentFormatImpl = new PaymentFormatImpl();

        Request req = new Request();

        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "acncc 234,22.55 (存入)", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "+ 234,22 ", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "acncc  4,078.00 (支出)", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "acncc ￥-2,000.45", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, " (存入)2,000.00", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "(存入)500.00", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "15,075.00", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "-890.00", ""));
        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "-4,820.00", ""));

        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "(消费)4,820.00", "存入,消费"));

        System.out.println("payment formate result: " + paymentFormatImpl.format(req, null, "(加入)4,820.00", "加入,消费"));
    }
}
