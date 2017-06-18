package com.flashvisions.server.red5.jsbridge.interfaces;

public interface IJSBridgeAware {
	
	void setRed5JSBridge(IJsBridge bridge);
	
	IJsBridge getRed5JSBridge();
}
