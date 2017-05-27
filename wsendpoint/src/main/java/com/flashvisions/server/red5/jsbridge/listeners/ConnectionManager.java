package com.flashvisions.server.red5.jsbridge.listeners;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketConnection;
import org.slf4j.Logger;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;

public class ConnectionManager {
	
	private CopyOnWriteArrayList<JsBridgeConnection> connections;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private int pingInterval = 30000;
	
	private static final Logger logger = Red5LoggerFactory.getLogger(ConnectionManager.class, "red5-js-bridge");

	
	
	public ConnectionManager(){
		
	}
	
	
	public void initialize()
	{
		connections = new CopyOnWriteArrayList<JsBridgeConnection>();
		scheduler.scheduleAtFixedRate(new Pinger(), 5000, pingInterval, TimeUnit.MILLISECONDS);
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
	    				break;
	    			}
				} 
	    		catch (Exception e) 
	    		{
					e.printStackTrace();
				}
	    	}
	    }		
	}
	
	
	
	
	
	public void sendToConnection(WebSocketConnection target, IMessage message) 
	{
		Iterator<JsBridgeConnection> iterator = connections.iterator();
		while (iterator.hasNext())
	    {
	    	JsBridgeConnection conn = iterator.next();
	    	if(conn != null && conn.isConnected())
	    	{
	    		try 
	    		{
	    			if(conn.getSignalChannel() == target)
	    			{
	    				conn.send(message);
	    				break;
	    			}
				} 
	    		catch (Exception e) 
	    		{
					e.printStackTrace();
				}
	    	}
	    }		
	}
	
	
	
	
	public static JsBridgeConnection getConnection(WebSocketConnection connection)
	{
		JsBridgeConnection conn = (JsBridgeConnection) connection.getSession().getAttribute(JsBridgeConnection.TAG);
		return conn;
	}
	
	
	
	
	public boolean addConnection(JsBridgeConnection connection)
	{
		if(!connections.contains(connection)){
			return connections.add(connection);
		}
		
		return false;
	}
	
	
	
	
	public boolean removeConnection(JsBridgeConnection connection)
	{
		boolean removed = false;
		
		Iterator<JsBridgeConnection> iterator = connections.iterator();
		while (iterator.hasNext())
	    {
			JsBridgeConnection conn = iterator.next();
	    	if(conn != null)
	    	{
	    		connections.remove(conn);
	    		removed = true;
	    		break;
	    	}
	    }
		
		return removed;
	}
	
	
	
	
	public void shutdown()
	{
		try
		{
			closeAllConnections();
		}
		catch(Exception e)
		{
			// NO OP
		}
		finally
		{
			connections.clear();
		}
		
		
		try
		{
			scheduler.shutdownNow();
		}
		catch(Exception e)
		{
			// NO OP
		}
	}

	
	
	
	class Pinger implements Runnable
	{

		@Override
		public void run() 
		{			
			Iterator<JsBridgeConnection> iterator = connections.iterator();
			while (iterator.hasNext())
		    {
				try
				{
					JsBridgeConnection conn = iterator.next();
					if(conn != null && conn.isConnected())
					{
						conn.ping();
					}
					else
					{
						logger.warn("Removing unresponsive connection {}",  conn);
						connections.remove(conn);
					}
				}
				catch(Exception e)
				{
					logger.error("Unknown error {}", e.getMessage());
				}
		    }
		}
		
	}
}
