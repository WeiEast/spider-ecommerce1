/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.format.unit;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 31, 2014 10:18:20 AM
 */
public enum NumberUnit {

    ONE {
        @Override
        public long getProportion() {
            return 1;
        }
    },
    TEN {
        @Override
        public long getProportion() {
            return 10;
        }
    },
    HUNDRED {
        @Override
        public long getProportion() {
            return 100;
        }
    },
    THOUDAND {
        @Override
        public long getProportion() {
            return 1000;
        }
    },
    TEN_THOUSAND {
        @Override
        public long getProportion() {
            return 10000;
        }
    },
    HUNDRED_THOUSAND {
        @Override
        public long getProportion() {
            return 100000;
        }
    },
    MILLION {
        @Override
        public long getProportion() {
            return 1000000;
        }
    };
   

    public abstract long getProportion();
    
    

}
