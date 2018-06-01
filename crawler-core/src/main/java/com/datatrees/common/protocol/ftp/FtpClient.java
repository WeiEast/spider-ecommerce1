/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol.ftp;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.Protocol;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.ProtocolOutput;
import crawlercommons.robots.BaseRobotRules;

/**
 *
 * @version 1.0
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @since Feb 9, 2014 4:26:18 PM
 */
public class FtpClient implements Protocol {

    /* (non-Javadoc)
     * @see com.datatrees.common.conf.Configurable#setConf(com.datatrees.common.conf.Configuration)
     */
    public void setConf(Configuration conf) {}

    /* (non-Javadoc)
     * @see com.datatrees.common.conf.Configurable#getConf()
     */
    public Configuration getConf() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.datatrees.vt.core.protocal.Protocol#getProtocolOutput(java.lang.String)
     */
    public ProtocolOutput getProtocolOutput(String url) {
        return getProtocolOutput(url, 0);
    }
    
    public ProtocolOutput getProtocolOutput(String url, long lastModified) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.datatrees.vt.core.protocal.Protocol#getRobotRules(java.lang.String)
     */
    public BaseRobotRules getRobotRules(String url) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.datatrees.vt.core.protocol.Protocol#getProtocolOutput(com.datatrees.vt.core.protocol.ProtocolInput)
     */
    @Override
    public ProtocolOutput getProtocolOutput(ProtocolInput input) {
        return ProtocolOutput.NULL ;
    }


}
