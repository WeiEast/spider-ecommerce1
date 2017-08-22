package com.datatrees.crawler.core.processor.format.container;

import com.datatrees.common.conf.DefaultConfiguration;
import org.junit.Test;

/**
 * @author Jerry
 * @since 22:46 21/05/2017
 */
public class NumberMapContainerTest {
    @Test
    public void get() throws Exception {
        NumberMapContainer container = NumberMapContainer.get(new DefaultConfiguration());
        System.out.println(container.getNumberMapper());
    }

}