package com.flashvisions.server.red5.jsbridge.model;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.red5.net.websocket.WebSocketConnection;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;
import com.flashvisions.server.red5.jsbridge.model.converter.MessageConverter;

public class JsBridgeConnection {
	
	public static String TAG = "JsBridgeConnection";
	
	WebSocketConnection signalChannel;
	
	String host;
	
	String remoteAddress;
	
	int remotePort;
	
	MessageConverter converter;
	
	
	
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
}
