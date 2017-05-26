package com.flashvisions.server.red5.jsbridge.listeners;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketConnection;
import org.red5.net.websocket.listener.WebSocketDataListener;
import org.red5.net.websocket.model.MessageType;
import org.red5.net.websocket.model.WSMessage;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.scope.GlobalScope;
import org.red5.server.scope.Scope;
import org.red5.server.scope.WebScope;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.flashvisions.server.red5.jsbridge.interfaces.IJSBridgeAware;
import com.flashvisions.server.red5.jsbridge.interfaces.IJsBridge;
import com.flashvisions.server.red5.jsbridge.model.EventMessage;
import com.flashvisions.server.red5.jsbridge.model.OutGoingMessage;
import com.flashvisions.server.red5.jsbridge.model.MessageStatus;
import com.flashvisions.server.red5.jsbridge.model.BridgeMessageType;

public class JsBridgeDataListener extends WebSocketDataListener implements IJsBridge, InitializingBean, ApplicationContextAware {

	private static final Logger logger = Red5LoggerFactory.getLogger(JsBridgeDataListener.class, "red5-js-bridge");
	
	private ApplicationContext applicationContext;
	
	private IScope appScope;
	
	private MultiThreadedApplicationAdapter appAdapter;
	
	private ConnectionManager connManager;


    {
        setProtocol("jsbridge");
    }
    
    
    public JsBridgeDataListener()
    {
    	connManager = new ConnectionManager();
    	connManager.initialize();
    }


	
	
	@Override
	public void onWSMessage(WSMessage message) 
	{
		
		final JsBridgeConnection connection = (JsBridgeConnection) message.getConnection().getSession().getAttribute(JsBridgeConnection.TAG);
		
		if (message.getMessageType() == MessageType.PING) 
		{
            logger.debug("Ping received, no processing required");
            return;
        }
		else if (message.getMessageType() == MessageType.PONG) 
		{
            return;
        }
		else if (message.getMessageType() != MessageType.CLOSE) 
		{
			// receive RMI requst here
			
			// verify message
			
			// invoke application method with reflection
		}
		else
		{
			logger.debug("closing connection {}", message.getConnection());
			connection.close();
		}
				
	}

	
	
	@Override
	public void onWSConnect(WebSocketConnection conn) 
	{
		JsBridgeConnection bridgeConnection = ConnectionManager.createBridgeConnectionObject(conn);
		connManager.addConnection(bridgeConnection);
	}

	
	
	@Override
	public void onWSDisconnect(WebSocketConnection conn) 
	{
		JsBridgeConnection bridgeConnection = ConnectionManager.getConnection(conn);
		connManager.addConnection(bridgeConnection);	
	}

	
	
	@Override
	public void stop() 
	{
		this.connManager.shutdown();
	}

	
	
	@Override
	public void pushMessage(Object data) throws Exception {
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.PUSH);
		message.setStatus(MessageStatus.DATA);
		message.setData(data);
		connManager.sendToAll(message);
	}
	
	
	
	
	
	@Override
	public void pushMessage(IConnection conn, Object data) throws Exception {
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.PUSH);
		message.setStatus(MessageStatus.DATA);
		message.setData(data);
		sendToIP(conn.getRemoteAddress(), message);
	}

	
	
	

	@Override
	public void broadcastEvent(String event, Object data) 
	{
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage(event, data)); // some how pack =>  close as special event
		connManager.sendToAll(message);
	}

	
	
	@Override
	public void close(String reason) 
	{
		// notify closing to client
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage("Closing", reason)); // some how pack =>  close as special event
		connManager.sendToAll(message);
		
		
		// close all connections
		
		connManager.closeAllConnections();		
	}

	
	
	@Override
	public void close() 
	{
		// notify closing to client
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage("Closing", null)); // some how pack =>  close as special event
		connManager.sendToAll(message);
		
		
		// close all connections
		
		connManager.closeAllConnections();
	}	
	
	
	
	
	private void sendToIP(String ip, OutGoingMessage message) 
	{
		connManager.sendToIP(ip, message);		
	}
	
	
	
	
	@Override
	public int getTotalConnection() 
	{
		return connManager.getTotalConnection();
	}

	
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)	throws BeansException {
		this.applicationContext = applicationContext;
	}

	
	
	
	@Override
	public void afterPropertiesSet() throws Exception 
	{
		 if (applicationContext.containsBean("web.scope")) 
		 {
	            appScope = (WebScope) applicationContext.getBean("web.scope");
	            logger.debug("Linked to app scope: {}", appScope);
	            appAdapter = (MultiThreadedApplicationAdapter) applicationContext.getBean("web.handler");
	            logger.debug("Linked to app: {}", appAdapter);
	     } 
		 else 
	     {
	            GlobalScope globalScope = (GlobalScope) applicationContext.getBean("global.scope");
	            if (globalScope == null) {
	                globalScope = new GlobalScope();
	            }
	            appScope = new Scope.Builder(globalScope, ScopeType.APPLICATION, "webrtc", false).build();
	            logger.debug("Built app scope {}", appScope);
	     }
		 
		 
		 if(appAdapter != null)
		 {
			 setBridgeOnApplication();
		 }
	}

	
	
	
	
	private void setBridgeOnApplication() 
	{
		if(appAdapter != null && appAdapter instanceof IJSBridgeAware) 
		{
			IJSBridgeAware ref = (IJSBridgeAware) appAdapter;
			ref.setRed5JSBridge(this);
		 }
	}



}
