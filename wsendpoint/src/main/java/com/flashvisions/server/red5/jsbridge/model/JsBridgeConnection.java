package com.flashvisions.server.red5.jsbridge.model;

import org.red5.net.websocket.WebSocketConnection;

public class JsBridgeConnection {
	
	WebSocketConnection internalConnection;
	
	
	public JsBridgeConnection()
	{
		
	}


	public WebSocketConnection getInternalConnection() {
		return internalConnection;
	}


	public void setInternalConnection(WebSocketConnection internalConnection) {
		this.internalConnection = internalConnection;
	}

	
	
}
