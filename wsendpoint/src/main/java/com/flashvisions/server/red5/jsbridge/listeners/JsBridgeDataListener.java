package com.flashvisions.server.red5.jsbridge.listeners;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketConnection;
import org.red5.net.websocket.listener.WebSocketDataListener;
import org.red5.net.websocket.model.WSMessage;
import org.slf4j.Logger;

import com.flashvisions.server.red5.jsbridge.interfaces.IJsBridgeMediator;

public class JsBridgeDataListener extends WebSocketDataListener implements IJsBridgeMediator {

	private static final Logger logger = Red5LoggerFactory.getLogger(JsBridgeDataListener.class, "red5-js-bridge");
	
	private CopyOnWriteArrayList<WebSocketConnection> connections = new CopyOnWriteArrayList<WebSocketConnection>();
    

    {
        setProtocol("jsbridge");
    }


	
	
	@Override
	public void onWSMessage(WSMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWSConnect(WebSocketConnection conn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWSDisconnect(WebSocketConnection conn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pushMessage(Object data) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void broadcastEvent(String event, Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() 
	{
		return getTotalConnection() > 0;
	}

	
	
	@Override
	public void close(String reason) 
	{
		// notify closing to client
		closeAllConnections();		
	}

	
	
	@Override
	public void close() 
	{
		// notify closing to client
		closeAllConnections();
	}

	
	
	private void closeAllConnections()
	{
		Iterator<WebSocketConnection> iterator = connections.iterator(); 
	    while (iterator.hasNext())
	    {
	    	WebSocketConnection conn = iterator.next();
	    	if(conn != null && conn.isConnected()){
	    		conn.close();
	    	}
	    }
	}
	
	
	
	
	@Override
	public int getTotalConnection() 
	{
		int i=0;
		Iterator<WebSocketConnection> iterator = connections.iterator(); 
	    while (iterator.hasNext()){
	    	
	    	WebSocketConnection conn = iterator.next();
	    	if(conn != null && conn.isConnected()){
	    		i++;
	    	}
	    }
	
		return i;
	}

}
