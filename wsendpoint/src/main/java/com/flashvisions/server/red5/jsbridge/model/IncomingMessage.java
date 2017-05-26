package com.flashvisions.server.red5.jsbridge.model;


public class IncomingMessage extends Message {
	
	
	public IncomingMessage()
	{
		super();
		setType(BridgeMessageType.RMI);
	}
	
}
