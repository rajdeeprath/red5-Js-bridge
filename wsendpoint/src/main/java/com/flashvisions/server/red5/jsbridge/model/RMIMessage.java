package com.flashvisions.server.red5.jsbridge.model;


public class RMIMessage extends IncomingMessage {
	
	private String method;
	
	public RMIMessage()
	{
		super();
		setType(BridgeMessageType.RMI);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	
	
	
}
