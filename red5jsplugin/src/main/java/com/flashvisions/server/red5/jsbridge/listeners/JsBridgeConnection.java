package com.flashvisions.server.red5.jsbridge.listeners;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketConnection;
import org.red5.net.websocket.model.MessageType;
import org.slf4j.Logger;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;
import com.flashvisions.server.red5.jsbridge.model.converter.MessageConverter;

public class JsBridgeConnection {
	
	private static final Logger logger = Red5LoggerFactory.getLogger(JsBridgeConnection.class, "red5-js-bridge");

	
	public static String TAG = "JsBridgeConnection";
	
	private WebSocketConnection signalChannel;
	
	private String host;
	
	private String remoteAddress;
	
	int remotePort;
	
	private MessageConverter converter;
	
	private String sessionId;
	
	private Map<String, Object> queryParams;
	
	
	
	public JsBridgeConnection()
	{
		converter = new MessageConverter();
	}


	public WebSocketConnection getSignalChannel() {
		return signalChannel;
	}


	public void setSignalChannel(WebSocketConnection signalChannel) {
		this.signalChannel = signalChannel;
	}


	public String getRemoteAddress() {
		return remoteAddress;
	}


	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}


	public int getRemotePort() {
		return remotePort;
	}


	public void setRemotePort(int i) {
		this.remotePort = i;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}

	
	public Map<String, Object>getConnectParams()
	{
		if(this.signalChannel != null)
		{
			return this.signalChannel.getQuerystringParameters();
		}
		
		return null;
	}
	
	
	public boolean isConnected()
	{
		return (this.signalChannel != null && this.signalChannel.isConnected());
	}


	
	public void send(IMessage message) throws UnsupportedEncodingException 
	{
		signalChannel.send(converter.toJson(message));		
	}


	public void close() 
	{
		signalChannel.close();	
	}
	
	
	
	
	public void pingAcknowledged()
	{
		
	}
	
	
	
	public void ping()
	{
		Object signalChannel = getSignalChannel();
		
        if (signalChannel instanceof WebSocketConnection) 
        {
        	try
        	{
	        	IoSession ioSession = ((WebSocketConnection) signalChannel).getSession();
	            if (ioSession.isConnected()) 
	            {
	            	long lastIo = ioSession.getLastWriteTime();
	                long delta = System.currentTimeMillis() - lastIo;
	             
	                if (delta > 60000L) 
	                {
	                    close();
	                } 
	                else 
	                {
	                    ioSession.write(org.red5.net.websocket.model.Packet.build("PING!".getBytes(), MessageType.PING));
	                    if(logger.isDebugEnabled())
	                    {
	                    	logger.info("Ping sent to {}", signalChannel);
	                    }
	                }
	            }
	            else 
	            {
	            	logger.warn("Cannot ping, not connected");
	            }
        	}
        	catch (Exception e) 
        	{
        		logger.warn("Exception on ping to {}", signalChannel);
            }
        }
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public Map<String, Object> getQueryParams() {
		return queryParams;
	}


	public void setQueryParams(Map<String, Object> queryParams) {
		this.queryParams = queryParams;
	}


	public boolean isApplicationAdapterConsumer(){
		if(queryParams.containsKey("adapterClient")){
			return (Boolean) queryParams.get("adapterClient");
		}
		
		return false;
	}
}
