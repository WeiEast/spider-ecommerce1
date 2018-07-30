package com.datatrees.rawdatacentral.extractor.actor;

import javax.annotation.Resource;

import com.datatrees.common.actor.AbstractActor;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by wuminlang on 15/7/29.
 */
@Service
@Scope("prototype")
public class ExtractorActor extends AbstractActor {

    private static final Logger        logger = LoggerFactory.getLogger(ExtractorActor.class);

    @Resource
    private              ExtractWorker extractWorker;

    /*
     * (non-Javadoc)
     *
     * @see com.datatrees.common.actor.AbstractActor#processMessage(java.lang.Object)
     */
    @Override
    public void processMessage(Object message) {
        logger.info("starting task worker for [" + message.toString() + "]");
        if (message instanceof ExtractMessage) {
            extractWorker.process((ExtractMessage) message);
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
        if (message instanceof ExtractMessage) {
            getSender().tell(((ExtractMessage) message));
        } else {
            unhandled(message);
        }

    }

}
