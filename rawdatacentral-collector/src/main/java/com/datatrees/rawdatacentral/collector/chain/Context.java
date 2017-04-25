
package com.datatrees.rawdatacentral.collector.chain;

import com.datatrees.common.pipeline.ContextBase;
import com.datatrees.common.pipeline.Request;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月29日 上午2:34:43 
 */
public class Context extends Request {

    ContextBase context;

    public Context(ContextBase context) {
        super();
        this.context = context;
    }

    public Context() {
        super();
    }

    public ContextBase getContext() {
        return context;
    }

    public void setContext(ContextBase context) {
        this.context = context;
    }
}
