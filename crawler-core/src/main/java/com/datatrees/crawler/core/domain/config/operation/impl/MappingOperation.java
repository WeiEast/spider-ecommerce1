/**
 * www.gf-dai.com.cn Copyright (c) 2015 All Rights Reserved.
 */
package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * 
 * @author likun
 * @version $Id: DateTimeOperation.java, v 0.1 Jul 22, 2015 11:58:37 AM likun Exp $
 */
@Tag("operation")
@Path(".[@type='mapping']")
public class MappingOperation extends AbstractOperation {
    /**
     *
     */
    private static final long serialVersionUID = 904657740887574781L;
    private String groupName;

    @Attr("group-name")
    public String getGroupName() {
        return groupName;
    }

    @Node("@group-name")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MappingOperation [groupName=" + groupName + "]";
    }

}
