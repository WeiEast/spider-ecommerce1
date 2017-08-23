package com.datatrees.rawdatacentral.collector.actor;

import com.datatrees.common.actor.AbstractActor;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Service
//@Scope("prototype")
@Deprecated
public class CollectorActor extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(CollectorActor.class);
    //    @Resource
    private Collector collector;

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.actor.ActorContext#processMessage(java.lang.Object)
     */
    @Override
    public void processMessage(Object message) {
        logger.info("starting task worker for [" + message.toString() + "]");
        if (message instanceof CollectorMessage) {
            collector.processMessage((CollectorMessage) message);
        } else {
            unhandled(message);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.actor.AbstractActor#processComplete(java.lang.Object)
     */
    @Override
    public void processComplete(Object message) {

    }

}
