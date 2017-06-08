package com.flashvisions.server.red5.jsbridge.model;


public class APIMessage extends IncomingMessage {
	
	public APIMessage()
	{
		super();
		setType(BridgeMessageType.API);
	}
}
