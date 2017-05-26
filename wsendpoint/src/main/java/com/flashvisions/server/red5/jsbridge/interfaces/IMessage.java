package com.flashvisions.server.red5.jsbridge.interfaces;

import com.flashvisions.server.red5.jsbridge.model.BridgeMessageType;

public interface IMessage {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract BridgeMessageType getType();

	public abstract void setType(BridgeMessageType type);

	public abstract Object getData();

	public abstract void setData(Object data);

	public abstract long getTimestamp();

	public abstract void setTimestamp(long timestamp);

}