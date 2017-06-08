package com.flashvisions.server.red5.jsbridge.model;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;

public interface IIncomingMessage extends IMessage {

	public abstract String getMethod();

	public abstract void setMethod(String method);

}