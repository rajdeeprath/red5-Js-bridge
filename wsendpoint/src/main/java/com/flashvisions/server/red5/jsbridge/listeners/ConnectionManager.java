package com.flashvisions.server.red5.jsbridge.listeners;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.red5.net.websocket.WebSocketConnection;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;
import com.flashvisions.server.red5.jsbridge.model.JsBridgeConnection;

public class ConnectionManager {
	
	private CopyOnWriteArrayList<JsBridgeConnection> connections = new CopyOnWriteArrayList<JsBridgeConnection>();

	
	public ConnectionManager(){
		
	}
	
	
	public void initialize()
	{
		
	}
	
	
	public static JsBridgeConnection createBridgeConnectionObject(WebSocketConnection conn)
	{
		JsBridgeConnection connection = new JsBridgeConnection();
		connection.setSignalChannel(conn);
		connection.setHost(conn.getHost());
		
		InetSocketAddress addr = (InetSocketAddress) conn.getSession().getRemoteAddress();
		connection.setRemoteAddress(addr.getAddress().getHostAddress());
		connection.setRemotePort(addr.getPort());
		
		conn.getSession().setAttribute(JsBridgeConnection.TAG, connection);

		return connection;
	}
	
	
	
	
	public void closeAllConnections()
	{
		Iterator<JsBridgeConnection> iterator = connections.iterator(); 
	    while (iterator.hasNext())
	    {
	    	JsBridgeConnection conn = iterator.next();
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
			WebSocketConnection conn = wrapper.getSignalChannel();
	    	if(conn != null && conn.isConnected()){
	    		i++;
	    	}
	    }
	
		return i;
	}
	
	
	
	
	public void sendToAll(IMessage message)
	{
		Iterator<JsBridgeConnection> iterator = connections.iterator();
		while (iterator.hasNext())
	    {
	    	JsBridgeConnection conn = iterator.next();
	    	if(conn != null && conn.isConnected())
	    	{
	    		try 
	    		{
					conn.send(message);
				} 
	    		catch (Exception e) 
	    		{
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	
	
	
	
	public void sendToIP(String ip, IMessage message) 
	{
		Iterator<JsBridgeConnection> iterator = connections.iterator();
		while (iterator.hasNext())
	    {
	    	JsBridgeConnection conn = iterator.next();
	    	if(conn != null && conn.isConnected())
	    	{
	    		try 
	    		{
	    			if(conn.getRemoteAddress().equals(ip))
	    			{
	    				conn.send(message);
	    			}
				} 
	    		catch (Exception e) 
	    		{
					e.printStackTrace();
				}
	    	}
	    }		
	}

}
