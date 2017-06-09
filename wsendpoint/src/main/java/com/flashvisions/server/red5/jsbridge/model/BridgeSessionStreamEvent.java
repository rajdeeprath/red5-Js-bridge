package com.flashvisions.server.red5.jsbridge.model;

import com.flashvisions.server.red5.jsbridge.alternate.model.BroadcastStream;
import com.flashvisions.server.red5.jsbridge.alternate.model.Connection;
import com.flashvisions.server.red5.jsbridge.alternate.model.Scope;

public class BridgeSessionStreamEvent {
	
	private String sessionId;
	
	private Scope scope;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	
}
