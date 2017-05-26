package com.flashvisions.server.red5.jsbridge.listeners;

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

public class JsBridgeDataListener extends WebSocketDataListener implements IJsBridge, InitializingBean, ApplicationContextAware {

	private static final Logger logger = Red5LoggerFactory.getLogger(JsBridgeDataListener.class, "red5-js-bridge");
	
	private CopyOnWriteArrayList<WebSocketConnection> connections = new CopyOnWriteArrayList<WebSocketConnection>();
    
	private ApplicationContext applicationContext;
	
	private IScope appScope;
	
	private MultiThreadedApplicationAdapter appAdapter;


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
	public void pushMessage(IConnection conn, Object data) throws Exception {
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
