package org.apache.james.mime4j.util;

import org.junit.Test;

/**
 * @author Jerry
 * @since 22:40 21/05/2017
 */
public class ContentUtilTest {
    @Test
    public void encode() throws Exception {
        String decode = ContentUtil.decode(new ByteArrayBuffer("揭秘信用卡提额骗局.htm".getBytes(), true));
        System.out.println(decode);

        ByteSequence encode = ContentUtil.encode(decode);
        System.out.println(encode);
    }

    @Test
    public void decode() throws Exception {
        String decode = ContentUtil.decode(new ByteArrayBuffer("揭秘信用卡提额骗局.htm".getBytes(), true));
        System.out.println(decode);
    }

}