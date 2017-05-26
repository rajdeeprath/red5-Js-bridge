package com.flashvisions.server.red5.jsbridge.interfaces;

import com.flashvisions.server.red5.jsbridge.model.MessageType;

public interface IMessage {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract MessageType getType();

	public abstract void setType(MessageType type);

	public abstract Object getData();

	public abstract void setData(Object data);

	public abstract long getTimestamp();

	public abstract void setTimestamp(long timestamp);

}