package com.flashvisions.server.red5.jsbridge.listeners;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.red5.net.websocket.WebSocketConnection;

import com.flashvisions.server.red5.jsbridge.model.JsBridgeConnection;
import com.flashvisions.server.red5.jsbridge.model.Message;
import com.flashvisions.server.red5.jsbridge.model.converter.MessageConverter;

public class ConnectionManager {
	
	private CopyOnWriteArrayList<JsBridgeConnection> connections = new CopyOnWriteArrayList<JsBridgeConnection>();

	
	public ConnectionManager(){
		
	}
	
	
	public void initialize()
	{
		
	}
	
	
	public static JsBridgeConnection getBridgeConnection(WebSocketConnection conn)
	{
		JsBridgeConnection connection = new JsBridgeConnection();
		connection.setInternalConnection(conn);
		
		return connection;
	}
	
	
	
	
	public void closeAllConnections()
	{
		Iterator<JsBridgeConnection> iterator = connections.iterator(); 
	    while (iterator.hasNext())
	    {
	    	JsBridgeConnection wrapper = iterator.next();
	    	WebSocketConnection conn = wrapper.getInternalConnection();
	    	if(conn != null && conn.isConnected()){
	    		conn.close();
	    	}
	    }
	}
	
	
	
	
	public int getTotalConnection() 
	{
		int i=0;
		Iterator<JsBridgeConnection> iterator = connections.iterator(); 
	    while (iterator.hasNext()){
	    	JsBridgeConnection wrapper = iterator.next();
			WebSocketConnection conn = wrapper.getInternalConnection();
	    	if(conn != null && conn.isConnected()){
	    		i++;
	    	}
	    }
	
		return i;
	}
	
	
	
	
	public void sendToAll(Message message)
	{
		MessageConverter converter = new MessageConverter();
		
		Iterator<JsBridgeConnection> iterator = connections.iterator();
		while (iterator.hasNext())
	    {
	    	JsBridgeConnection wrapper = iterator.next();
			WebSocketConnection conn = wrapper.getInternalConnection();
	    	if(conn != null && conn.isConnected()){
	    		try {
					conn.send(converter.toJson(message));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	
	
	
	
	public void sendToIP(String ip, Message message) 
	{
		MessageConverter converter = new MessageConverter();
		
		Iterator<JsBridgeConnection> iterator = connections.iterator();
		while (iterator.hasNext())
	    {
	    	JsBridgeConnection wrapper = iterator.next();
			WebSocketConnection conn = wrapper.getInternalConnection();
	    	if(conn != null && conn.isConnected()){
	    		try {
					conn.send(converter.toJson(message));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }		
	}

}
