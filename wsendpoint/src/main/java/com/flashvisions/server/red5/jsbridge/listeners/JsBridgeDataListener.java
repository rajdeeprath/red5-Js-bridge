package com.flashvisions.server.red5.jsbridge.listeners;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketConnection;
import org.red5.net.websocket.listener.WebSocketDataListener;
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
import com.flashvisions.server.red5.jsbridge.model.JsBridgeConnection;
import com.flashvisions.server.red5.jsbridge.model.Message;
import com.flashvisions.server.red5.jsbridge.model.MessageStatus;
import com.flashvisions.server.red5.jsbridge.model.MessageType;

public class JsBridgeDataListener extends WebSocketDataListener implements IJsBridge, InitializingBean, ApplicationContextAware {

	private static final Logger logger = Red5LoggerFactory.getLogger(JsBridgeDataListener.class, "red5-js-bridge");
	
	//private CopyOnWriteArrayList<WebSocketConnection> connections = new CopyOnWriteArrayList<WebSocketConnection>();
    
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
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public void onWSConnect(WebSocketConnection conn) 
	{
		JsBridgeConnection bridgeConnection = ConnectionManager.getBridgeConnection(conn);
		
		// validate and add to connection manager list
		
	}

	@Override
	public void onWSDisconnect(WebSocketConnection conn) {
		
		// remove from connect manager list
		
	}

	
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	
	
	@Override
	public void pushMessage(Object data) throws Exception {
		
		Message message = new Message();
		message.setType(MessageType.PUSH_MESSAGE);
		message.setStatus(MessageStatus.DATA);
		message.setData(data);
		connManager.sendToAll(message);
	}
	
	
	
	
	
	@Override
	public void pushMessage(IConnection conn, Object data) throws Exception {
		
		Message message = new Message();
		message.setType(MessageType.PUSH_MESSAGE);
		message.setStatus(MessageStatus.DATA);
		message.setData(data);
		sendToIP(conn.getRemoteAddress(), message);
	}

	
	
	

	@Override
	public void broadcastEvent(String event, Object data) 
	{
		Message message = new Message();
		message.setType(MessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage(event, data)); // some how pack =>  close as special event
		connManager.sendToAll(message);
	}

	
	
	@Override
	public void close(String reason) 
	{
		// notify closing to client
		
		Message message = new Message();
		message.setType(MessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage("closing", reason)); // some how pack =>  close as special event
		connManager.sendToAll(message);
		
		
		// close all connections
		
		connManager.closeAllConnections();		
	}

	
	
	@Override
	public void close() 
	{
		// notify closing to client
		
		Message message = new Message();
		message.setType(MessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage("closing", null)); // some how pack =>  close as special event
		connManager.sendToAll(message);
		
		
		// close all connections
		
		connManager.closeAllConnections();
	}	
	
	
	
	
	private void sendToIP(String ip, Message message) 
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
