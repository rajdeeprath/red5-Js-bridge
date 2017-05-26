package com.flashvisions.server.red5.jsbridge.interfaces;

import org.red5.server.api.IConnection;

public interface IJsBridge {

	public void pushMessage(Object data) throws Exception;
	public void pushMessage(IConnection conn, Object data) throws Exception;
	public void broadcastEvent(String event, Object data);
	public int getTotalConnection();
	public void close(String reason);
	public void close();
}
