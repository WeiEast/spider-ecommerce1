/**
 * datatrees.com Inc.
 * Copyright (c) 2004-${year} All Rights Reserved.
 */
package com.datatrees.common.protocol.file;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.Protocol;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.ProtocolOutput;
import crawlercommons.robots.BaseRobotRules;

/**
 * 
 * @version 1.0
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @since Feb 9, 2014 4:26:35 PM
 */
public class FileClient implements Protocol {

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.conf.Configurable#setConf(com.datatrees.common.conf.Configuration)
     */
    public void setConf(Configuration conf) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.conf.Configurable#getConf()
     */
    public Configuration getConf() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.vt.core.protocal.Protocol#getProtocolOutput(java.lang.String)
     */
    public ProtocolOutput getProtocolOutput(String url) {
        return getProtocolOutput(url, 0);
    }

    public ProtocolOutput getProtocolOutput(String url, long lastModified) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.vt.core.protocal.Protocol#getRobotRules(java.lang.String)
     */
    public BaseRobotRules getRobotRules(String url) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.vt.core.protocol.Protocol#getProtocolOutput(com.datatrees.vt.core.protocol.ProtocolInput
     * )
     */
    @Override
    public ProtocolOutput getProtocolOutput(ProtocolInput input) {
        return ProtocolOutput.NULL;
    }

}
