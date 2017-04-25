package com.datatrees.rawdatacentral.core.message;

import com.alibaba.rocketmq.common.message.MessageExt;

public class SimpleMessageReceiver extends AbstractRocketMessageListener {

	@Override
	public void process(Object message) {
		System.out.println(message.toString());
	}

    /* (non-Javadoc)
     * @see AbstractRocketMessageListener#messageConvert(com.alibaba.rocketmq.common.message.MessageExt)
     */
    @Override
    public Object messageConvert(MessageExt message) {
        return null;
    }

}
