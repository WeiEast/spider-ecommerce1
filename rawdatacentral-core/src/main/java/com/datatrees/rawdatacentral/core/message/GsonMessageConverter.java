package com.datatrees.rawdatacentral.core.message;

import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.ReflectionUtils;
import com.datatrees.rawdatacentral.core.common.Constants;

public class GsonMessageConverter implements MessageConverter {
	private String topic;
	private int flag = 0;
	private String tags = "";
	private String keys = "";
	private boolean waitStoreMsgOK = true;
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public boolean isWaitStoreMsgOK() {
		return waitStoreMsgOK;
	}

	public void setWaitStoreMsgOK(boolean waitStoreMsgOK) {
		this.waitStoreMsgOK = waitStoreMsgOK;
	}

	@Override
	public Message toMessage(Object object) throws Exception {
		String body = GsonUtils.toJson(object);
		Message message = new Message(topic, tags, keys, flag, body.getBytes(), waitStoreMsgOK);
		message.putUserProperty(Constants.GSON_TYPE, object.getClass().getName());
		return message;
	}

	@Override
	public Object fromMessage(Message message) throws Exception {
		String gsonType = message.getUserProperty(Constants.GSON_TYPE);
		Class<?> type = ReflectionUtils.classForName(gsonType);
		return GsonUtils.fromJson(new String(message.getBody()), type);
	}
}
