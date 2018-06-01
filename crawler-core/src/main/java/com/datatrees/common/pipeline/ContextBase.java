/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.pipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:10:36 PM
 */
public class ContextBase implements Context, Pipeline {

    protected Pipeline            pipeline = new StandardPipeline();

    protected Map<String, Object> context  = new HashMap<String, Object>();

    public void invoke(Request request, Response response) throws Exception {
        pipeline.getFirst().invoke(request, response);
    }

    @Override
    public Valve getBasic() {
        return pipeline.getBasic();
    }

    @Override
    public void setBasic(Valve valve) {
        pipeline.setBasic(valve);
    }

    @Override
    public void addValve(Valve valve) {
        pipeline.addValve(valve);
    }

    @Override
    public Valve[] getValves() {
        return pipeline.getValves();
    }

    @Override
    public void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }

    @Override
    public Valve getFirst() {
        return pipeline.getFirst();
    }

    @Override
    public Object getAttribute(String id) {
        return context.get(id);
    }

    public String getString(String id) {
        return (String) context.get(id);
    }

    @Override
    public void setAttribute(String id, Object obj) {
        context.put(id, obj);
    }

    @Override
    public Object removeAttribute(String id) {
        return context.remove(id);
    }

}
