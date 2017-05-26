package com.flashvisions.server.red5.jsbridge.interfaces;

public interface IJsBridgeMediator {

	public void pushMessage(Object data) throws Exception;
	public void broadcastEvent(String event, Object data);
	public boolean isConnected();
	public int getTotalConnection();
	public void close(String reason);
	public void close();
}
