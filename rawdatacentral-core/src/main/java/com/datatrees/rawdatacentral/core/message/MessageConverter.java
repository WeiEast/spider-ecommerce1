package com.datatrees.rawdatacentral.core.message;

import org.apache.rocketmq.common.message.Message;

public interface MessageConverter {

    /**
     * Convert a Java object to a Message.
     * @param object the object to convert
     * @return the Message
     */
    Message toMessage(Object object) throws Exception;

    /**
     * Convert from a Message to a Java object.
     * @param message the message to convert
     * @return the converted Java object
     */
    Object fromMessage(Message message) throws Exception;

}
