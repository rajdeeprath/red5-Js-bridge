package com.flashvisions.server.red5.jsbridge.model;


public class RMIMessage extends IncomingMessage {
	
	
	public RMIMessage()
	{
		super();
		setType(BridgeMessageType.RMI);
	}
}
