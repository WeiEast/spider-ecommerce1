package com.datatrees.crawler.core.processor.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.impl.decode.DecodeType;
import org.junit.Test;

/**
 * @author Jerry
 * @since 22:55 21/05/2017
 */
public class DecodeOperationImplTest {

    @Test
    public void decode() throws Exception {
        System.out.println(DecodeOperationImpl.decode("\\u005f\\u0028\\u003a\\u437\\u300d\\u2220\\u0029\\u005f\\u0020\\u4e00\\u672c\\u6b63\\u7ecf\\u7684\\u80e1\\u8bf4\\u516b\\u9053\\u0028\\u5218\\u5b8f\\u4f1f\\u0029", DecodeType.STANDARD, "utf-8"));
    }

}