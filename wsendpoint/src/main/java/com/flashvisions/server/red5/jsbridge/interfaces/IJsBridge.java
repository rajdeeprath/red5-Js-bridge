package com.flashvisions.server.red5.jsbridge.interfaces;

import org.red5.server.api.IConnection;

import com.flashvisions.server.red5.jsbridge.listeners.JsBridgeConnection;

public interface IJsBridge {

	public void pushMessage(Object data) throws Exception;
	public void pushMessage(IConnection conn, Object data) throws Exception;
	public int getTotalConnection();
	public void close(String reason);
	public void close();
	public void broadcastEvent(String event, Object data);
	public void broadcastEvent(JsBridgeConnection target, String event, Object data);
}
